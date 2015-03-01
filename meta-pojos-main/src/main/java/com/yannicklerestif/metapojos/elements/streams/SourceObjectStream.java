package com.yannicklerestif.metapojos.elements.streams;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.SourceObject;

public abstract class SourceObjectStream<T extends SourceObject, U extends SourceObjectStream<T,U>> {
	protected Stream<T> stream;

	public Stream<T> stream() {
		return stream;
	}

	public SourceObjectStream(Stream<T> stream) {
		super();
		this.stream = stream;
	}
	
	protected abstract U wrap(Stream<T> stream);
	
	public void print() {
		this.stream.forEach(System.out::println);
	}
	
	public U matches(String pattern) {
		return filter(sourceObject -> sourceObject.toString().contains(pattern));
	}

	public U recursive(UnaryOperator<U> operation) {
		Set<T> startingStock = new HashSet<T>();
		Set<T> newElements = new HashSet<T>();
		stream.forEach(sourceObject -> {
			startingStock.add(sourceObject);
			newElements.add(sourceObject);
		});
		applyRecursively(startingStock, newElements, operation);
		return wrap(startingStock.stream());
	}
	
	private void applyRecursively(Set<T> previousStock,
			Set<T> previousNewElements, UnaryOperator<U> operation) {
		Set<T> newElements = new HashSet<T>();
		operation.apply(wrap(previousNewElements.stream())).foreach(sourceObject -> {
			if(!(previousStock.contains(sourceObject))) {
				newElements.add(sourceObject);
				previousStock.add(sourceObject);
			}
		});
		if(newElements.size() > 0)
			applyRecursively(previousStock, newElements, operation);
	}

	//-------------------------------------------- stream methods ----------------------------------------
	
	public U filter(Predicate<? super T> predicate) {
		return wrap(stream.filter(predicate));
	}
	
	public void foreach(Consumer<? super T> action) {
		stream.forEach(action);
	}
	
	public U sorted() {
		return wrap(stream.sorted(Comparator.comparing(sourceObject -> sourceObject.toString())));
	}
	

}
