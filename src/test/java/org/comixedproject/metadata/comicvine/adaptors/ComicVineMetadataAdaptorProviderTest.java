package org.comixedproject.metadata.comicvine.adaptors;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineMetadataAdaptorProviderTest {
  private static final String TEST_GOOD_ADDRESS_1 =
      "https://comicvine.gamespot.com/action-comics-futures-end-1-crossroads/4000-463937/";
  private static final String TEST_GOOD_ADDRESS_2 =
      "https://www.comicvine.com/action-comics-futures-end-1-crossroads/4000-463937/";
  private static final String TEST_BAD_ADDRESS =
      "https://notcomicvine.gamespot.com/action-comics-futures-end-1-crossroads/4000-463937";

  @InjectMocks private ComicVineMetadataAdaptorProvider provider;

  @Test
  public void testSupportedReferenceWithBadReference() {
    assertFalse(provider.supportedReference(TEST_BAD_ADDRESS));
  }

  @Test
  public void testSupportedReferenceOldHttpComicVineDomain() {
    assertTrue(provider.supportedReference(TEST_GOOD_ADDRESS_2.replace("https", "http")));
  }

  @Test
  public void testSupportedReferenceOldComicVineDomain() {
    assertTrue(provider.supportedReference(TEST_GOOD_ADDRESS_2));
  }

  @Test
  public void testSupportedReferenceCurrentHttpComicVineDomain() {
    assertTrue(provider.supportedReference(TEST_GOOD_ADDRESS_1.replace("https", "http")));
  }

  @Test
  public void testSupportedReferenceCurrentComicVineDomain() {
    assertTrue(provider.supportedReference(TEST_GOOD_ADDRESS_1));
  }
}
