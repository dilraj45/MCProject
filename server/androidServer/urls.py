from django.conf.urls import url
from .views import SOSRequestHandler, get_open_sos_requests, get_allocated_sos_requests, authenticate_user_credentials
from .views import save_user_credentials, update_lat_long, post_sos_request, delete_sos_request

urlpatterns = [
    #url(r'^$', SOSRequestHandler.as_view())
    url(r'^PostSOSRequest/$', post_sos_request),
    url(r'^UpdateLocation/$', update_lat_long),
    url(r'^DeleteSOSRequest/$', delete_sos_request),
    url(r'^AuthenticateCred/$', authenticate_user_credentials),
    url(r'^SignUp/$', save_user_credentials),
    url(r'^OpenSOSRequest/$', get_open_sos_requests)
]
