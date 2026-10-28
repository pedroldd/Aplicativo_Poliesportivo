from django.contrib.auth.models import AbstractUser
from django.db import models

class User(AbstractUser):
    """Conta de autenticação identificada pelo CPF."""

    user_cpf = models.CharField(max_length=11, primary_key=True)
    user_nickname = models.CharField(max_length=100, blank=True)
    user_age = models.PositiveSmallIntegerField()

    def __str__(self):
        return self.username