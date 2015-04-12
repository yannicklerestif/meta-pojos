package com.yannicklerestif.metapojos.model.elements.streams;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.yannicklerestif.metapojos.model.elements.beans.JavaElementBean;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public abstract class JavaElementStream<T extends JavaElementBean, U extends JavaElementStream<T, U>> {

	//TODO Javadoc for methods in JavaElementStream and child classes

	protected Stream<T> stream;

	public Stream<T> stream() {
		return stream;
	}

	public JavaElementStream(Stream<T> stream) {
		super();
		this.stream = stream;
	}

	protected abstract U wrap(Stream<T> stream);

	public void print() {
		this.stream.forEach(element -> {
			PluginAccessor.getPlugin().getConsole().printJavaElementBean(element);
		});
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

	private void applyRecursively(Set<T> previousStock, Set<T> previousNewElements, UnaryOperator<U> operation) {
		Set<T> newElements = new HashSet<T>();
		operation.apply(wrap(previousNewElements.stream())).streamForeach(sourceObject -> {
			if (!(previousStock.contains(sourceObject))) {
				newElements.add(sourceObject);
				previousStock.add(sourceObject);
			}
		});
		if (newElements.size() > 0)
			applyRecursively(previousStock, newElements, operation);
	}

	//-------------------------------------------- stream methods ----------------------------------------

	public U streamFilter(Predicate<? super T> predicate) {
		return wrap(stream.filter(predicate));
	}

	public void streamForeach(Consumer<? super T> action) {
		stream.forEach(action);
	}

	public U streamSorted() {
		return wrap(stream.sorted(Comparator.comparing(sourceObject -> sourceObject.toString())));
	}

}
