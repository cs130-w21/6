## JSON format for frontend-backend coomunication

```java:
uploadRequest = {
    "op" : "upload"
    "data" : <String> image
}
uploadResponse = {
    "op" : "upload"
    "quote" : <String>[] quotes
}
```

```java:
 hasError = {
    "op" : "fail"
 }
```

```java:
registerRequest = {
    "op" : "register"
    "uid" : <String> username
    "password" : <String> password
}
registerResponse = {
    "op" : "register"
    "success" : <Integer> success (1: success, 0: fail)
    "uid": <String> username
}
```

```java:
loginRequest = {
    "op" : "login"
    "uid" : <String> username
    "password" : <String> password
}
loginResponse = {
    "op" : "login"
    "success" : <Integer> 0/1
    "uid": <String> username
}
```

```java:
loadRequest = {
    "op" : "load"
    "uid" : <String> username
}
loadResponse = {
    "op" : "load"
    "data" : <JSONArray> = [<JSONObject> story1, <JSONObject> story2, ...]
}
```

```java:
confirmRequest = {
    "op" : "confirm"
    "uid" : <String> username
    "image" : <String> encodedImage
    "quote" : <String> confirmedQuote
}
confirmResponse = {
    "op" : "confirm"
}
```

```java:
deleteRequest = {
    "op" : "delete"
    "row_id" : <Integer>[] = [row_id1, row_id2, ....]
    "uid": <String> username
}
deleteResponse = {
    "op" : "delete"
    "data" : <JSONArray> = [<JSONObject> story1, <JSONObject> story2, ...]
}
```

## Possible features for CI/CD stage
- [ ] user crop photo before upload
- [ ] user delete multiple stories
- [ ] user post event to nearby users
