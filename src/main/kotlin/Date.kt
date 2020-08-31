import java.lang.Exception
import java.time.Month

class Date {
    //TODO lots of multiple date formats go here
    fun parsePossibleDate(dateAsString: String){

    }
}
//read month from our custom string we use
fun parseDate(month: String): Int{
    val monthUpperCase=month.toUpperCase()
    val monthMap = listOf(
        "JAN",
        "FEB",
        "MAR",
        "APR",
        "MAY",
        "JUN",
        "JUL",
        "AUG",
        "SEP",
        "NOV",
        "SEP",
        "DEC"
    )
    var pos=monthMap.indexOf(monthUpperCase)
    //if not in list, attempt to check for full name, if that fails, throw exception
    if(pos==-1) {
        try {
            pos = Month.valueOf(monthUpperCase).value
        }catch (nim: Exception){
            throw Exception("Month invalid")
        }
    }
    return pos;
}