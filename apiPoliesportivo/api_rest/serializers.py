from rest_framework import serializers

from .models import User


class UserSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, min_length=8)
    username = serializers.CharField(required=False, allow_blank=True)

    class Meta:
        model = User
        fields = (
            "username",
            "password",
            "user_cpf",
            "user_nickname",
            "first_name",
            "last_name",
            "email",
            "user_age",
        )

    def create(self, validated_data):
        password = validated_data.pop("password")
        validated_data["username"] = validated_data.get("username") or validated_data["user_cpf"]
        return User.objects.create_user(password=password, **validated_data)
