/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2020, The ComiXed Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses>
 */

package org.comixedproject.metadata.comicvine.adaptors;

import static junit.framework.TestCase.*;
import static org.comixedproject.metadata.comicvine.adaptors.ComicVineMetadataAdaptorProvider.PROPERTY_API_KEY;

import java.util.*;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetIssueAction;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetIssueDetailsAction;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetVolumesAction;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.comixedproject.metadata.model.IssueMetadata;
import org.comixedproject.metadata.model.VolumeMetadata;
import org.comixedproject.model.metadata.MetadataSource;
import org.comixedproject.model.metadata.MetadataSourceProperty;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ComicVineMetadataAdaptorTest {
  private static final Random RANDOM = new Random();
  private static final String TEST_API_KEY = "TEST.API.KEY";
  private static final String TEST_SERIES_NAME = "Super Awesome ComicBook";
  private static final Integer TEST_MAX_RECORDS = RANDOM.nextInt();
  private static final String TEST_VOLUME_ID = "129";
  private static final String TEST_ISSUE_NUMBER = "17";
  private static final String TEST_ISSUE_ID = "327";
  private static final String TEST_REFERENCE_ID = "1083732";
  private static final String TEST_WEB_ADDRESS =
      String.format(
          "%s-%s",
          "https://comicvine.gamespot.com/action-comics-futures-end-1-crossroads/4000",
          TEST_REFERENCE_ID);

  private final List<VolumeMetadata> volumeMetadataList = new ArrayList<>();
  private final List<IssueMetadata> issueMetadataList = new ArrayList<>();
  private final Set<MetadataSourceProperty> metadataSourceProperties = new HashSet<>();

  @InjectMocks private ComicVineMetadataAdaptor adaptor;
  @Mock private ComicVineGetVolumesAction getVolumesAction;
  @Mock private ComicVineGetIssueAction getIssueAction;
  @Mock private ComicVineGetIssueDetailsAction getIssueDetailsAction;
  @Mock private VolumeMetadata volumeMetadata;
  @Mock private IssueMetadata issueMetadata;
  @Mock private IssueDetailsMetadata issueDetailsMetadata;
  @Mock private MetadataSource metadataSource;

  @Before
  public void setUp() {
    Mockito.when(metadataSource.getProperties()).thenReturn(metadataSourceProperties);
    metadataSourceProperties.add(
        new MetadataSourceProperty(metadataSource, PROPERTY_API_KEY, TEST_API_KEY));
  }

  @Test(expected = MetadataException.class)
  public void testGetVolumes_missingApiKey() throws MetadataException {
    metadataSourceProperties.clear();

    try {
      adaptor.doGetVolumes(TEST_SERIES_NAME, TEST_MAX_RECORDS, metadataSource, getVolumesAction);
    } finally {
      Mockito.verify(getVolumesAction, Mockito.times(1))
          .setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    }
  }

  @Test(expected = MetadataException.class)
  public void testGetVolumes_unsetApiKey() throws MetadataException {
    metadataSourceProperties.stream()
        .filter(property -> property.getName().equals(PROPERTY_API_KEY))
        .findFirst()
        .get()
        .setValue("");

    try {
      adaptor.doGetVolumes(TEST_SERIES_NAME, TEST_MAX_RECORDS, metadataSource, getVolumesAction);
    } finally {
      Mockito.verify(getVolumesAction, Mockito.times(1))
          .setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    }
  }

  @Test
  public void testGetVolumes_noResults() throws MetadataException {
    Mockito.when(getVolumesAction.execute()).thenReturn(volumeMetadataList);

    final List<VolumeMetadata> result =
        adaptor.doGetVolumes(TEST_SERIES_NAME, TEST_MAX_RECORDS, metadataSource, getVolumesAction);

    assertNotNull(result);
    assertTrue(result.isEmpty());

    Mockito.verify(getVolumesAction, Mockito.times(1))
        .setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setSeries(TEST_SERIES_NAME);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setMaxRecords(TEST_MAX_RECORDS);
  }

  @Test
  public void testGetVolumes() throws MetadataException {
    for (int index = 0; index < 200; index++) volumeMetadataList.add(volumeMetadata);

    Mockito.when(getVolumesAction.execute()).thenReturn(volumeMetadataList);

    final List<VolumeMetadata> result =
        adaptor.doGetVolumes(TEST_SERIES_NAME, TEST_MAX_RECORDS, metadataSource, getVolumesAction);

    assertNotNull(result);
    assertEquals(volumeMetadataList.size(), result.size());

    Mockito.verify(getVolumesAction, Mockito.times(1))
        .setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setSeries(TEST_SERIES_NAME);
    Mockito.verify(getVolumesAction, Mockito.times(1)).setMaxRecords(TEST_MAX_RECORDS);
  }

  @Test
  public void testGetIssue_noResults() throws MetadataException {
    Mockito.when(getIssueAction.execute()).thenReturn(issueMetadataList);

    final IssueMetadata result =
        adaptor.doGetIssue(TEST_VOLUME_ID, TEST_ISSUE_NUMBER, metadataSource, getIssueAction);

    assertNull(result);

    Mockito.verify(getIssueAction, Mockito.times(1)).setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getIssueAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueAction, Mockito.times(1)).setVolumeId(TEST_VOLUME_ID);
  }

  @Test
  public void testGetIssue() throws MetadataException {
    issueMetadataList.add(issueMetadata);

    Mockito.when(getIssueAction.execute()).thenReturn(issueMetadataList);

    final IssueMetadata result =
        adaptor.doGetIssue(TEST_VOLUME_ID, TEST_ISSUE_NUMBER, metadataSource, getIssueAction);

    assertNotNull(result);
    assertSame(issueMetadata, result);

    Mockito.verify(getIssueAction, Mockito.times(1)).setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getIssueAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueAction, Mockito.times(1)).setVolumeId(TEST_VOLUME_ID);
    Mockito.verify(getIssueAction, Mockito.times(1)).setIssueNumber(TEST_ISSUE_NUMBER);
  }

  @Test
  public void testGetIssue_issueNumberHasLeadingZeroes() throws MetadataException {
    issueMetadataList.add(issueMetadata);

    Mockito.when(getIssueAction.execute()).thenReturn(issueMetadataList);

    final IssueMetadata result =
        adaptor.doGetIssue(
            TEST_VOLUME_ID, "0000" + TEST_ISSUE_NUMBER, metadataSource, getIssueAction);

    assertNotNull(result);
    assertSame(issueMetadata, result);

    Mockito.verify(getIssueAction, Mockito.times(1)).setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getIssueAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueAction, Mockito.times(1)).setVolumeId(TEST_VOLUME_ID);
    Mockito.verify(getIssueAction, Mockito.times(1)).setIssueNumber("0000" + TEST_ISSUE_NUMBER);
  }

  @Test
  public void testGetIssue_issueNumberIsZero() throws MetadataException {
    issueMetadataList.add(issueMetadata);

    Mockito.when(getIssueAction.execute()).thenReturn(issueMetadataList);

    final IssueMetadata result =
        adaptor.doGetIssue(TEST_VOLUME_ID, "0", metadataSource, getIssueAction);

    assertNotNull(result);
    assertSame(issueMetadata, result);

    Mockito.verify(getIssueAction, Mockito.times(1)).setBaseUrl(ComicVineMetadataAdaptor.BASE_URL);
    Mockito.verify(getIssueAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueAction, Mockito.times(1)).setVolumeId(TEST_VOLUME_ID);
    Mockito.verify(getIssueAction, Mockito.times(1)).setIssueNumber("0");
  }

  @Test
  public void testGetIssueDetails_noResults() throws MetadataException {
    Mockito.when(getIssueDetailsAction.execute()).thenReturn(issueDetailsMetadata);

    final IssueDetailsMetadata result =
        adaptor.doGetIssueDetails(TEST_ISSUE_ID, metadataSource, getIssueDetailsAction);

    assertNotNull(result);
    assertSame(issueDetailsMetadata, result);

    Mockito.verify(getIssueDetailsAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueDetailsAction, Mockito.times(1)).setIssueId(TEST_ISSUE_ID);
  }

  @Test
  public void testGetIssueDetails() throws MetadataException {
    Mockito.when(getIssueDetailsAction.execute()).thenReturn(issueDetailsMetadata);

    final IssueDetailsMetadata result =
        adaptor.doGetIssueDetails(TEST_ISSUE_ID, metadataSource, getIssueDetailsAction);

    assertNotNull(result);
    assertSame(issueDetailsMetadata, result);

    Mockito.verify(getIssueDetailsAction, Mockito.times(1)).setApiKey(TEST_API_KEY);
    Mockito.verify(getIssueDetailsAction, Mockito.times(1)).setIssueId(TEST_ISSUE_ID);
  }

  @Test
  public void testGetReferenceId() {
    final String result = adaptor.getReferenceId(TEST_WEB_ADDRESS);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(TEST_REFERENCE_ID, result);
  }
}
