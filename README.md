#  Mechatg
> Test Domen https://mechatg.herokuapp.com/

## Api for Posts
``` 

/api/posts/all  | GET | barcha uchun
/api/posts/add     | POST faqat userlar va admin
/api/posts/delete/{id} | DELETE  faqat kommentariya egasi va admin 
/api/posts/update | PUT faqat kommentariya egasi | id bodyda jo'natilsin 
```
 > Barchasi PROTETED API (/all dan tashqari)
 
 > Post uchun yuboriladigan ma'lumotlar
 ``` 
 {multipart/form-data}:
   * message: String,
   * replyPost: String, (default{null})
   * file: File (default{null}| image || video || gif)
```
> Post malumotlarini o'zgartirish uchun (for PUT method)
```
  * id: String,
  * message: String
```
## Api for Post Files
```
/api/files/all | GET admin
/api/files/preview/{id} | GET barcha uchun
/api/files/download/{id} | GET barcha uchun
```
> Protected API (../all)

## Api for Owner
```
/api/owner/admin/add  | POST 
/api/owner/admin/add/{id} | PUT userlarga adminlik huquqini berish
/api/owner/admins     | GET
/api/owner/admin/delete/{id} | DELETE
/api/owner/admin/updatedetails/{id} | PUT
```
## Api For Admin
```
/api/admin/user/add  | POST 
/api/admin/users     | GET | role==admin va role==owner 
/api/admin/user/delete/{id} | DELETE
/api/admin/user/updatedetails/{id} | PUT
/api/admin/updatedetails | PUT 
``` 
> Admin malumotlari uchun bodyda yuborilishi kerak bo'lgan ma'lumotlar
``` body:
* fullname: String,
* username: String,
* phoneNumber: String,
* password: String,
* about: String,
* facebook: String,
* telegram: String,
* instagram: String
```

> Admin va Owner uchun headerda barcha zaproslarda token(barchasi PROTECTED API lardir)

## Api For Auth
```
/api/auth/register  | POST
/api/auth/login     | POST
/api/auth/getme   | GET 
/api/auth/user/delete  | DELETE
/api/auth/user/updatedetails | PUT
***
/api/auth/user/forgotpassword  | POST | phone
/api/auth/user/checkcode/{code} | POST | phone va code
/api/auth/user/editpassword  | POST | phone, code, password


/api/auth/user/forgotpassword  | POST | phone
body:
  phone
  

/api/auth/user/checkcode/{code}  | POST | phone va code

body:
  phone

/api/auth/user/editpassword  | POST | phone, code, password

body:
  password
  
header:
  phone: String,
  code: String

```
> Protected APIs
```
/api/auth/getme  | GET
/api/auth/user/delete  | DELETE
/api/auth/user/updatedetails | PUT 
```
> Register User
```
* phoneNumber: String,
* fullname: String,
* username: String,
* password: String
```
> Login
```
* username,
* password
```
> Minimal password uzunligi: 6
> Maximal file size: 1MB

> Protected API larda HEADER da token jo`natish kerak

``` 
Authorization: Bearer token 
```
