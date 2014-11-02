package org.apodhrad.phrasedroid;

import android.annotation.SuppressLint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;

/**
 * 
 * @author apodhrad
 * 
 */
public class Repository {

	public static final String GIT_SUFFIX = ".git";

	private static Repository INSTANCE;

	private String remoteRepository;
	private File localRepository;

	private List<File> markdownFiles;

	public Repository(String remoteRepository, String folder) {
		this.remoteRepository = remoteRepository;
		this.localRepository = new File(folder, getRepositoryName(remoteRepository));
		markdownFiles = new ArrayList<File>();
	}

	public static Repository getInstance(String folder) throws InvalidRemoteException, TransportException,
			GitAPIException, IOException {
		return getInstance(folder, true);
	}

	public static Repository getInstance(String folder, boolean internetConnection) throws InvalidRemoteException,
			TransportException, GitAPIException, IOException {
		if (INSTANCE == null) {
			INSTANCE = new Repository("https://github.com/apodhrad/phrasedroid.wiki.git", folder);
			if (internetConnection) {
				INSTANCE.update();
			}
		}
		INSTANCE.updateMarkdownFiles();
		return INSTANCE;
	}

	public void update() throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		if (localRepository.exists()) {
			pull();
		} else {
			create();
		}
	}

	@SuppressLint("DefaultLocale")
	public void updateMarkdownFiles() {
		if (!exists()) {
			return;
		}
		markdownFiles = new ArrayList<File>();
		File[] files = localRepository.listFiles();
		for (File file : files) {
			if (file != null && file.isFile() && file.getName().endsWith(".md")) {
				if (file.getName().toLowerCase().startsWith("home")) {
					continue;
				}
				if (file.getName().toLowerCase().startsWith("index")) {
					continue;
				}
				markdownFiles.add(file);
			}
		}
	}

	public void pull() throws IOException, WrongRepositoryStateException, InvalidConfigurationException,
			DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException, NoHeadException,
			TransportException, GitAPIException {
		FileRepository localRepo = new FileRepository(new File(localRepository, GIT_SUFFIX));
		Git git = new Git(localRepo);
		git.pull().call();
	}

	public void create() throws InvalidRemoteException, TransportException, GitAPIException {
		localRepository.delete();
		Git result = Git.cloneRepository().setURI(remoteRepository).setDirectory(localRepository).call();
		try {
			result.getRepository().getDirectory();
		} finally {
			result.close();
		}
	}

	public static String getRepositoryName(String repositoryUrl) {
		String[] parts = repositoryUrl.split("/");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].endsWith(GIT_SUFFIX)) {
				return parts[i].substring(0, parts[i].length() - GIT_SUFFIX.length());
			}
		}
		return null;
	}

	public boolean exists() {
		return localRepository.exists();
	}

	public File getFile(int index) {
		return markdownFiles.get(index);
	}

	public File[] getFiles() {
		return markdownFiles.toArray(new File[markdownFiles.size()]);
	}

}
