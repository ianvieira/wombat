package org.ambraproject.wombat.config;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ThemeSource {

  private final File root;

  public ThemeSource(File root) {
    Preconditions.checkArgument(root.exists());
    this.root = root;
  }

  public static final class SiteSpec {
  }

  public static final class ThemeSpec {
    private final String key;
    private final File path;
    private final ImmutableList<String> parentKeys;

    private ThemeSpec(String key, File path, List<String> parentKeys) {
      this.key = Objects.requireNonNull(key);
      this.path = Objects.requireNonNull(path);
      this.parentKeys = ImmutableList.copyOf(parentKeys);
    }

    @Override
    public boolean equals(Object o) {
      return this == o || o != null && getClass() == o.getClass()
          && key.equals(((ThemeSpec) o).key)
          && path.equals(((ThemeSpec) o).path)
          && parentKeys.equals(((ThemeSpec) o).parentKeys);
    }

    public String getKey() {
      return key;
    }

    public File getPath() {
      return path;
    }

    public ImmutableList<String> getParentKeys() {
      return parentKeys;
    }

    @Override
    public int hashCode() {
      return 31 * (31 * key.hashCode() + path.hashCode()) + parentKeys.hashCode();
    }
  }

  public List<SiteSpec> readSites() {
    return null;
  }

  private Collection<File> findAllThemeDirectories() throws IOException {
    Collection<File> hits = Collections.synchronizedCollection(new ArrayList<>());
    Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().equalsIgnoreCase("theme.yaml")) {
          hits.add(file.toFile());
        }
        return super.visitFile(file, attrs);
      }
    });
    return hits;
  }

  private static ThemeSpec buildThemeSpec(File file) throws IOException {
    try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8))) {
      new Yaml().load(reader)
    }
  }

  public Collection<ThemeSpec> readThemes() throws IOException {
    return findAllThemeDirectories().stream()
        .map(ThemeSource::buildThemeSpec)
        .sorted(Comparator.comparing(ThemeSpec::getKey))
        .collect(Collectors.toList());
  }

}
