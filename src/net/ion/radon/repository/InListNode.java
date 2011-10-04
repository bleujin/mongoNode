package net.ion.radon.repository;

import java.util.Map;

import net.ion.framework.util.ChainMap;

public interface InListNode {
	InListQuery createQuery();

	InListNode push(ChainMap<String, ?> values);
	InListNode push(Map<String, ?> values);

	InListNode insertBefore(String target, Map<String, Object> values);

	InListNode insertAfter(String target, Map<String, Object> values);

	InListNode update(String id, Map<String, Object> values);
}
