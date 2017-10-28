from django.conf.urls import url
from .views import SOSRequestHandler, get_open_sos_requests, get_allocated_sos_requests, authenticate_user_credentials
from .views import save_user_credentials, update_lat_long, post_sos_request, delete_sos_request

urlpatterns = [
    #url(r'^$', SOSRequestHandler.as_view())
    url(r'^$', delete_sos_request)
]