// org.example.library.annotations/Audited.java
package org.example.library.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    String action() default "ACCESS";
}