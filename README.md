#  Mechatg
> Test Domen https://mechatg.herokuapp.com/

## Api for Posts
``` 
/api/posts/all  | GET faqat userlar va admin
/api/posts/add     | POST faqat userlar va admin
/api/posts/delete/{id} | DELETE  faqat kommentariya egasi va admin 
/api/posts/update | PUT faqat kommentariya egasi | id bodyda jo'natilsin 
  ```
 > Post uchun bodyda yuboriladigan ma'lumotlar
 ``` body:
   * description: String _
```
## Api for Owner
```
/api/owner/admin/add  | POST 
/api/owner/admins     | GET
/api/owner/admin/delete/{id} | DELETE
/api/owner/admin/updatedetails/{id} | PUT
```
## Api For Admin
```
/api/admin/user/add  | POST 
/api/admin/users     | GET
/api/admin/user/delete/{id} | DELETE
/api/admin/user/updatedetails/{id} | PUT
/api/admin/updatedetails | PUT 
``` 
> Admin uchun bodyda yuborilishi kerak bo'lgan ma'lumotlar
``` body:
* fullname: String 
* username: String
* password: String,
* about: String,
* facebook: String,
* telegram: String,
* instagram: String
```

> Admin va Owner uchun headerda barcha zaproslarda token(barchasi protected api lardir)

## Api For Auth
```
/api/auth/register  | POST
/api/auth/login     | POST
/api/auth/getme   | GET 
/api/auth/user/delete  | DELETE
/api/auth/user/updatedetails | PUT 
```
> Protected APIs
```
/api/auth/getme  | GET
/api/auth/user/delete  | DELETE
/api/auth/user/updatedetails | PUT 
```
> Register User
```
* fullname: String 
* username: String
* password: String
```
> Login
```
* username,
* password
```
> Protected API larda HEADER da token jo`natish kerak

``` 
Authorization: Bearer token 
```
