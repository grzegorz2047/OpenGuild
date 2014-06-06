Wymagania
-----------

* Kodowanie UTF-8.
* Należy używać 4 spacji zamiast tabów.
* Nie używamy tagu `@author`.
* Używamy poniższego formatu kodu.
```
/*
 *
 * LICENSE HEADER
 *
 */

package pl.grzegorz2047.openguild;

import com.github.grzegorz2047.openguild.OpenGuild;
import java.util.ArrayList;
import java.util.HashMap;
import pl.openguild2047.openguild.commands.GuildCommand;

public class TestClass {
    
    private static final HashMap<Object, Object> hashMap = new HashMap<>();
    
    public void testVoid(boolean arg1, String arg2, int arg3, double arg4) {
        if(arg1) {
            // Do stuff
        }
        else if(false) {
            // Do stuff
        } else {
            // Do stuff
        }
        
        try {
            // Do stuff
        } catch(Exception ex) {
            // Do stuff
        }
    }
    
}

```
* Zalecamy używać środowiska [NetBeans IDE 7](https://netbeans.org/downloads/) lub nowszy oraz programu [Git](http://git-scm.com/downloads) (domyślnie wbudowany w NetBeans). Do kompilacji używamy [Maven](http://maven.apache.org/download.cgi) (domyślnie wbudowany w NetBeans).
