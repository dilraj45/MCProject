from django.contrib import admin
from .models import User, SessionHandle, SOSRequest, SOSRequestResolver
# Register your models here.


class UserAdmin(admin.ModelAdmin):
    list_display = ('id', 'username', 'password', 'contact', 'latitude', 'longitude')

class SOSRequestAdmin(admin.ModelAdmin):
    list_display = ('id', 'message', 'user')

class SOSRequestResolverAdmin(admin.ModelAdmin):
    list_display = ('id', 'user_id' , 'sos_request')

class SessionHandelAdmin(admin.ModelAdmin):
    list_display = ('id', 'user_id', 'authentication_token')


admin.site.register(SessionHandle, SessionHandelAdmin)
admin.site.register(SOSRequest, SOSRequestAdmin)
admin.site.register(SOSRequestResolver, SOSRequestResolverAdmin)
admin.site.register(User, UserAdmin)