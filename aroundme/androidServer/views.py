from decimal import Decimal
from django.http import Http404
from django.http import HttpResponse
from django.utils.crypto import get_random_string
from django.views import View
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
from django.http import JsonResponse
from .models import User, SessionHandel, SOSRequest, SOSRequestResolver
from math import sin, cos, sqrt, atan2, radians
import json


class SOSRequestHandler(View):

    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(SOSRequestHandler, self).dispatch(request, *args, **kwargs)

    def get(self, request, *args, **kwargs):
        print ("Request: " + str(request.GET))
        return JsonResponse({'success': 'true'})


def compute_distance(lat1, lon1, lat2, lon2):
    R = 6373.0
    lat1 = radians(lat1)
    lon1 = radians(lon1)
    lat2 = radians(lat2)
    lon2 = radians(lon2)
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat / 2) ** 2 + cos(lat1) * cos(lat2) * sin(dlon / 2) ** 2
    c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c


def get_open_sos_requests(request):
    if request.method == "POST":
        raise Http404
    payload = request.GET
    auth_token = payload.get('token')
    distance = payload.get('distance', default=1)
    open_sos_requests = []
    try:
        user = SessionHandel.objects.get(authentication_token = auth_token).user
        allocated_sos_request = []
        for resolve_request in SOSRequestResolver.objects.filter(user=user.id):
            allocated_sos_request.append(resolve_request.sos_request)
        # todo: use values with dict = false
        for request in SOSRequest.objects.all():
            if compute_distance(user.latitude, user.longitude, request.user.latitude, request.user.longitude) \
                    <= distance and request not in allocated_sos_request and user.id != request.user.id:
                json_response = {
                    'message': request.message,
                    'username': request.user.username,
                    'contact': request.user.contact
                }
                open_sos_requests.append(json_response)
    except Exception as e:
        print(e)
    return JsonResponse({"OpenSOSRequests": open_sos_requests})


def get_allocated_sos_requests(request):
    if request.method == "POST":
        raise Http404
    payload = request.GET
    auth_token = payload.get('token')
    allocated_sos_request = []
    try:
        user = SessionHandel.objects.get(authentication_token=auth_token).user
        for resolve_request in SOSRequestResolver.objects.filter(user=user.id):
            json_response = {
                'message': resolve_request.sos_request.message,
                'username': resolve_request.user.username,
                'contact': resolve_request.user.contact
            }
            allocated_sos_request.append(json_response)
    except SessionHandel.DoesNotExist as e:
        print(e)
    return JsonResponse({"AllocatedSOSRequests": allocated_sos_request})


@method_decorator(csrf_exempt)
def authenticate_user_credentials(request):
    if request.method == "GET":
        raise Http404
    payload = request.POST
    username = payload.get('username')
    password = payload.get('password')
    try:
        user = User.objects.get(username=username)
        if password == user.password:
            # todo: change token to uuid to avoid collision or add exception handling
            token = get_random_string(length=32)
            session = SessionHandel( user=user, authentication_token=token)
            session.save()
            return JsonResponse({'token':token})
    except User.DoesNotExist as e:
        print(e)
    return JsonResponse({})


@method_decorator(csrf_exempt)
def save_user_credentials(request):
    if request.method == "GET":
        raise Http404
    payload = request.POST
    username = payload.get('username')
    password = payload.get('password')
    contact = payload.get('contact')
    latitude = payload.get('latitude')
    longitude = payload.get('longitude')
    try:
        user = User(username=username, password=password, contact=contact, latitude=latitude, longitude=longitude)
        user.save()
        return JsonResponse({'Signup':'Success'})
    except Exception as e:
        print(e)
    return JsonResponse({})


@method_decorator(csrf_exempt)
def update_lat_long(request):
    if request.method == "GET":
        payload = request.GET
    else:
        payload = json.loads(request.body.decode('utf-8'))

    token = payload.get('token')
    lat = payload.get('latitude')
    long = payload.get('longitude')
    session_handel = SessionHandel.objects.get(authentication_token=token)
    user = session_handel.user
    user.latitude = lat
    user.longitude = long
    print(user.id)
    user.save()
    return JsonResponse({"Update":"Success"})


@method_decorator(csrf_exempt)
def post_sos_request(request):
    if request.method == "GET":
        payload = request.GET
    else:
        payload = json.loads(request.body.decode('utf-8'))

    token = payload.get('token')
    message = payload.get('message')
    handler = SessionHandel.objects.get(authentication_token=token)
    request = SOSRequest(message=message, user=handler.user)
    request.save()
    return JsonResponse({"RequestId": request.id})


@method_decorator(csrf_exempt)
def delete_sos_request(request):
    if request.method == "GET":
        payload = request.GET
    else:
        payload = request.POST
    token = payload.get('token')
    user = SessionHandel.objects.get(authentication_token=token).user
    SOSRequest.objects.filter(user=user).delete()
    return HttpResponse("SOS Request successfully deleted!")
    print(e)
