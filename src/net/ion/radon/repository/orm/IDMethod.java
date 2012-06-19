package net.ion.radon.repository.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) 

public @interface IDMethod {
	String workspaceName() default "myspace";
	String groupId() ;
	String keyPropId();
	Class<? extends NodeORM> managerClz() ;
}
