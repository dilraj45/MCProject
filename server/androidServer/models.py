import uuid
from django.db import models


class User(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid1, editable=False)
    username = models.CharField(unique=True, max_length=50)
    password = models.CharField(max_length=32)
    contact = models.CharField(unique=True, max_length=32)
    # email = models.CharField(max_length=50)
    latitude = models.DecimalField(max_digits=10, decimal_places=6, null=True, blank=True)
    longitude = models.DecimalField(max_digits=10, decimal_places=6, null=True, blank=True)


class SOSRequest(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid1, editable=False)
    message = models.CharField(max_length=100, null=True)
    user = models.OneToOneField(User, unique=True, on_delete=models.CASCADE)


class SOSRequestResolver(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid1, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    sos_request = models.ForeignKey(SOSRequest, on_delete=models.CASCADE)

    class Meta:
        unique_together = ['user', 'sos_request']


class SessionHandel(models.Model):
    id = models.UUIDField(primary_key=True, default=uuid.uuid1, editable=False)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    authentication_token = models.CharField(max_length=32, unique=True)