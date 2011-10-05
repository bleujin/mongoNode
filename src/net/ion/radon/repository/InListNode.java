package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.ChainMap;

public interface InListNode {
	InListQuery createQuery();

	InListNode push(ChainMap values);
	InListNode push(Map<String, ?> values);
}
