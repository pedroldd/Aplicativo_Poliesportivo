from django.contrib.auth import authenticate, get_user_model
from rest_framework import status
from rest_framework.authtoken.models import Token
from rest_framework.decorators import api_view, authentication_classes, permission_classes
from rest_framework.permissions import IsAuthenticated
from rest_framework.authentication import TokenAuthentication
from rest_framework.response import Response

from .serializers import UserSerializer

User = get_user_model()


@api_view(["POST"])  # Aceita requisições POST para cadastrar uma conta.
def cadastro(request):
    serializer = UserSerializer(data=request.data)  # Valida os dados enviados no JSON.

    if not serializer.is_valid():  # Verifica se os campos passaram pela validação.
        return Response(
            serializer.errors,  # Devolve os erros encontrados.
            status=status.HTTP_400_BAD_REQUEST,  # Informa que os dados enviados são inválidos.
        )

    usuario = serializer.save()  # Cria o usuário; o create() do serializer chama create_user() e aplica o hash da senha.

    token, _ = Token.objects.get_or_create(
        user=usuario  # Busca um token existente ou cria um para esse usuário.
    )

    return Response(
        {
            "token": token.key,  # Envia o token para o aplicativo usar nas próximas requisições.
            "user": UserSerializer(usuario).data,  # Devolve os dados públicos do usuário, sem a senha.
        },
        status=status.HTTP_201_CREATED,  # Informa que a conta foi criada.
    )



@api_view(["POST"])
def login(request):
    username = request.data.get("username")
    password = request.data.get("password")
    if not username or not password:
        return Response(
            {"detail": "Informe username e password."},
            status=status.HTTP_400_BAD_REQUEST,
        )

    user = authenticate(request, username=username, password=password)
    if user is None:
        return Response(
            {"detail": "Usuário ou senha inválidos."},
            status=status.HTTP_401_UNAUTHORIZED,
        )

    token, _ = Token.objects.get_or_create(user=user)
    return Response({"token": token.key, "user": UserSerializer(user).data})


@api_view(["GET"])
@authentication_classes([TokenAuthentication])
@permission_classes([IsAuthenticated])
def teste_token(request):
    return Response({"detail": "Token válido.", "username": request.user.username})
