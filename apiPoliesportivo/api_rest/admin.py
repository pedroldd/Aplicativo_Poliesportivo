from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from .models import User

from django.contrib import admin
from django.contrib.auth.admin import UserAdmin

from .models import User


@admin.register(User)
class CustomUserAdmin(UserAdmin):
    fieldsets = UserAdmin.fieldsets + (
        (
            "Perfil poliesportivo",
            {
                "fields": (
                    "user_cpf",
                    "user_nickname",
                    "user_age",
                )
            },
        ),
    )

    add_fieldsets = UserAdmin.add_fieldsets + (
        (
            "Perfil poliesportivo",
            {
                "fields": (
                    "user_cpf",
                    "user_nickname",
                    "user_age",
                )
            },
        ),
    )

    list_display = (
        "username",
        "user_cpf",
        "email",
        "is_staff",
    )

    search_fields = (
        "username",
        "user_cpf",
        "email",
        "first_name",
        "last_name",
    )