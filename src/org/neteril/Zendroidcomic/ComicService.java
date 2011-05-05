package org.neteril.Zendroidcomic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.neteril.Zendroidcomic.ComicSource.DilbertComicSource;
import org.neteril.Zendroidcomic.ComicSource.GarfieldComicSource;
import org.neteril.Zendroidcomic.ComicSource.LolcatComicSource;
import org.neteril.Zendroidcomic.ComicSource.PhdComicSource;
import org.neteril.Zendroidcomic.ComicSource.TrollcatComicSource;
import org.neteril.Zendroidcomic.ComicSource.XkcdComicSource;

public class ComicService {
	public enum Available {
		Xkcd,
		Garfield,
		Lolcat,
		Dilbert,
		PhdComic,
		Trollcats;
		
		public int toIntIndex () {
			switch (this) {
			case Xkcd:
				return 0;
			case Garfield:
				return 1;
			case Lolcat:
				return 2;
			case Dilbert:
				return 3;
			case PhdComic:
				return 4;
			case Trollcats:
				return 5;
			default:
				return -1;	
			}
		}
	}
	
	// Fuck you Java
	public interface ComicCallback {
		public void run(ComicInformations comic);
	}
	// Fuck you Java²
	public class ComicRunnable implements Runnable {
		private ComicCallback callback;
		private IComicSource source;
		public ComicRunnable (IComicSource source, ComicCallback callback) {
			this.callback = callback;
			this.source = source;
		}
		public void run() {
			callback.run(source.getNextComic());
		}
	}
	
	private IComicSource[] sources = { 
			new XkcdComicSource(),
			new GarfieldComicSource(),
			new LolcatComicSource(),
			new DilbertComicSource(),
			new PhdComicSource(),
			new TrollcatComicSource()
		};
	private boolean[] disabledComics = new boolean[sources.length];
	private List<IComicSource> shuffled = new ArrayList<IComicSource> ();
	private Random rnd = new Random();
	
	private ListIterator<IComicSource> enumerator;
	
	ExecutorService taskExecutor = Executors.newCachedThreadPool();
	
	public void getNextComic (ComicCallback callback) {
		if (enumerator == null || !enumerator.hasNext())
			shuffleSources();
		IComicSource current = enumerator.next();

		taskExecutor.execute(new ComicRunnable(current, callback));
	}
	
	public void toggleComicAvailability (Available comic) {
		int index = comic.toIntIndex();
		if (index == -1)
			return;
		disabledComics[index] = !disabledComics[index];
	}
	
	void shuffleSources () {
		shuffled.clear();
		for (int i = 0; i < sources.length; i++) {
			if (disabledComics[i])
				continue;
			IComicSource addin = sources[i];
			if (shuffled.size() == 0)
				shuffled.add(addin);
			else
				shuffled.add(rnd.nextInt(shuffled.size()), addin);
		}
		enumerator = shuffled.listIterator();
	}
}
