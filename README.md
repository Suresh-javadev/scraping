url: localhost:18080/scrap
<br>
method: POST
<br>
requestBody:
<br>
contentType: application/json
<br>
body:
<br>
{
    "username":"youruser",
    "password":"yourpassword",
    "fromdate":"",
    "todate":"",
    "asOnDate":true,
    "amc":"123",
    "filetype":"mfsd246",
    "foliolist":[
        "12345",
        "56789"
    ]

}
<br>
Currently only mfsd246 supported.
<br>
mdsd246: folio wise transaction report
<br>
required amc wise folio record.
<br>

on success you will get refno
