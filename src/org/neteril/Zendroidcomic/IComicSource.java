package org.neteril.Zendroidcomic;

public interface IComicSource {
	ComicInformations getNextComic ();
	String getComicName ();
	String getComicAuthor ();
	boolean ShouldCachePicture ();
}
