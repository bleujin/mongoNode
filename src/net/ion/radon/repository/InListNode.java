package net.ion.radon.repository;

import java.util.Map;

public interface InListNode {
	InListQuery createQuery();

	void push(Map<String, ?> values);
}
