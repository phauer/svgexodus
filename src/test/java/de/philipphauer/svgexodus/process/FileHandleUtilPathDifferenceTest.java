package de.philipphauer.svgexodus.process;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

public class FileHandleUtilPathDifferenceTest {

	@Test
	public void getParentFolderName$Folder() {
		Path sourceFile = Paths.get("C:/svgfolder/file.svg");
		String prefix = FileHandleUtil.getParentFolderName(sourceFile);
		Assert.assertEquals("svgfolder", prefix);
	}

	@Test
	public void getParentFolderName$Subfolder() {
		Path sourceFile = Paths.get("C:/svgfolder/foo/file.svg");
		String prefix = FileHandleUtil.getParentFolderName(sourceFile);
		Assert.assertEquals("foo", prefix);
	}

	@Test
	public void getParentFolderName$Root() {
		Path sourceFile = Paths.get("C:/file.svg");
		String prefix = FileHandleUtil.getParentFolderName(sourceFile);
		Assert.assertEquals("", prefix);
	}
}
