package net.ion.repository.mongo.convert;

import java.lang.reflect.Field;

import net.ion.repository.mongo.node.ReadNode;

public abstract class TypeAdaptor<T> {
	public abstract T read(TypeStrategy ts, Field field, ReadNode node)  ;

}



