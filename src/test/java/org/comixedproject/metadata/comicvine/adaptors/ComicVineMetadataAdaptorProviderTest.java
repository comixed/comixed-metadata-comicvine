package org.comixedproject.metadata.comicvine.adaptors;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineMetadataAdaptorProviderTest {
  private static final String TEST_GOOD_ADDRESS =
      "https://comicvine.gamespot.com/action-comics-futures-end-1-crossroads/4000-463937/";
  private static final String TEST_BAD_ADDRESS =
      "https://notcomicvine.gamespot.com/action-comics-futures-end-1-crossroads/4000-463937";;

  @InjectMocks private ComicVineMetadataAdaptorProvider provider;

  @Test
  public void testSupportedReferenceWithBadReference() {
    assertFalse(provider.supportedReference(TEST_BAD_ADDRESS));
  }

  @Test
  public void testSupportedReference() {
    assertTrue(provider.supportedReference(TEST_GOOD_ADDRESS));
  }
}
