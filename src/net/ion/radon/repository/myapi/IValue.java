package net.ion.radon.repository.myapi;

import java.util.Calendar;

import net.ion.radon.core.EnumClass.IValueType;

public interface IValue {
	public String getString() ;
	public long getLong() ;
	public Calendar getDate() ;
	public double getDouble() ;
	public boolean getBoolean() ; 
	public IValueType getType() ;
}
