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
    "username":"x",
    "password":"y",
    "fromdate":"",
    "todate":"",
    "asOnDate":true,
    "amc":"RMF",
    "zipPassword":"abcd1234",
    "filetype":"mfsd246",
    "foliolist":[
        "123456",
        "567859"
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
