package com.mikehoffert.easyappend.control;

import static org.mockito.Mockito.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import com.mikehoffert.easyappend.model.BufferedFile;

public class TestController
{
	BufferedFile file1 = mock(BufferedFile.class);
	BufferedFile file2 = mock(BufferedFile.class);
	TextAddition prepend = mock(TextAddition.class);
	TextAddition append = mock(TextAddition.class);
	
	@Before
	public void setup() throws FileNotFoundException, IOException
	{
		// Thus, we're prepending some text (doesn't matter what) if the
		// file contains "foo". Both files contain foo. We're appending
		// if the file does NOT contain "bar". Only file2 contains bar.
		when(prepend.getText()).thenReturn("foo");
		when(prepend.getContains()).thenReturn("foo");
		when(prepend.isInverted()).thenReturn(false);
		when(prepend.isPrepend()).thenReturn(true);

		when(append.getText()).thenReturn("bar");
		when(append.getContains()).thenReturn("bar");
		when(append.isInverted()).thenReturn(true);
		when(append.isPrepend()).thenReturn(false);
		
		when(file1.contains("foo")).thenReturn(true);
		when(file1.contains("bar")).thenReturn(false);
		
		when(file2.contains("foo")).thenReturn(true);
		when(file2.contains("bar")).thenReturn(true);
	}
	
	@Test
	public void testWriteFiles() throws FileNotFoundException, IOException
	{
		Controller controller = spy(new Controller());
		controller.addFile(file1);
		controller.addFile(file2);
		controller.addText(prepend);
		controller.addText(append);
		
		when(file1.getFile()).thenReturn(new File("test1"));
		when(file2.getFile()).thenReturn(new File("test2"));
		
		controller.writeFiles();
		
		verify(file1).setPrependText("foo", false);
		verify(file1).setAppendText("bar", false);
		
		verify(file2).setPrependText("foo", false);
		verify(file2, never()).setAppendText("bar", false);
	}
}
