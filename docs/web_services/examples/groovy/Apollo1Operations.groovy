
import groovy.json.JsonSlurper
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import groovy.sql.Sql

@Grab(group = 'org.json', module = 'json', version = '20140107')
@Grab(group='postgresql', module='postgresql', version='8.3-603.jdbc4')
//@Grab('org.postgresql:postgresql:9.3-1101-jdbc41')
@GrabConfig(systemClassLoader = true)




static def getFeature(url,track,cookieFile){

//    curl -b demo_cookies.txt -c demo_cookies.txt -e "http://icebox.lbl.gov/WebApolloDemo/" --data "{ 'operation': 'get_features', 'track': 'Annotations-Group1.10'}" http://icebox.lbl.gov/WebApolloDemo/AnnotationEditorService
    String json = "{ 'operation': 'get_features', 'track': '${track}'}"
    def process = ["curl","-b",cookieFile,"-c",cookieFile,"-e",url,"--data",json,"${url}/AnnotationEditorService"].execute()
    def response = process.text
    if(process.exitValue()!=0){
        println process.errorStream.text
    }
    def jsonResponse = new JsonSlurper().parseText(response)
    return jsonResponse

}

static def doLogin(url, username, password,cookieFile) {
    String json = "{'username': '${username}', 'password': '${password}'}"
    def process = ["curl", "-c", cookieFile, "-H", "Content-Type:application/json", "-d", json, "${url}/Login?operation=login"].execute()
    def response = process.text
    if (process.exitValue() != 0) {
        println process.errorStream.text
    }
    def jsonResponse = new JsonSlurper().parseText(response)
    return jsonResponse
}

static def getUsers(username,password,url){
    JSONArray usersArray = new JSONArray()
    Class.forName("org.postgresql.Driver");

//    Sql sql = groovy.sql.Sql.newInstance( "jdbc:postgresql://localhost/web_apollo_users",username,password, "org.postgresql.Driver")
    Sql sql = groovy.sql.Sql.newInstance( "jdbc:postgresql://${url}",username,password, "org.postgresql.Driver")
    sql.eachRow('select * from users') { row ->
        println row
    }

    return usersArray
}
