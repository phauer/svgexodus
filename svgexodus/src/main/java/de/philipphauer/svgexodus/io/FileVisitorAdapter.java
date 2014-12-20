package de.philipphauer.svgexodus.io;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Philipp Hauer
 */
public class FileVisitorAdapter<T> implements FileVisitor<T> {

	@Override
	public FileVisitResult postVisitDirectory(T dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult preVisitDirectory(T dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(T file, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(T file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
