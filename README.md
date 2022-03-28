url: localhost:18080/scrap
method: POST

requestBody:
contentType: application/json
body:
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

Currently only mfsd246 supported.
mdsd246: folio wise transaction report
required amc wise folio record.

on success you will get refno
