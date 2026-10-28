from django.urls import path

from . import views

urlpatterns = [
    path("cadastro/", views.cadastro, name="cadastro foda"),
    path("login/", views.login, name="login"),
    path("teste_token/", views.teste_token, name="teste_token"),
]
