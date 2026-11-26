"""meiduo_mall URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""

from django.urls import path
from . import views

urlpatterns = [
    # 判断用户名是否重复
    path('usernames/<username:username>/count/', views.UsernameCountView.as_view()),
    path('mobiles/<mobile:mobile>/count/', views.MobileCountView.as_view()),
    path('register/', views.RegisterView.as_view()), # 注册
    path('login/', views.LoginView.as_view()),
    path('logout/', views.LogoutView.as_view()),
    path('center/', views.CenterView.as_view()),
    path('info/', views.CenterView.as_view()),
    path('email/', views.EmailView.as_view()),
    path('addresses/create/',views.CreateAddressView.as_view()),
    path('addresses/',views.AddressView.as_view()),
    path('addresses/<address_id>/',views.UpdateDestroyAddressView.as_view()),
    path('addresses/<address_id>/default/',views.DefaultAddressView.as_view()),
    path('addresses/<address_id>/title/',views.UpdateTitleAddressView.as_view()),
    path('password/',views.ChangePasswordView.as_view()),
]


