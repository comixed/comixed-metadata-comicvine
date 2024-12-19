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

import static org.comixedproject.metadata.comicvine.adaptors.ComicVineMetadataAdaptorProvider.*;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.MetadataException;
import org.comixedproject.metadata.adaptors.AbstractMetadataAdaptor;
import org.comixedproject.metadata.adaptors.MetadataAdaptor;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetAllIssuesAction;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetIssueAction;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetIssueDetailsAction;
import org.comixedproject.metadata.comicvine.actions.ComicVineGetVolumesAction;
import org.comixedproject.metadata.model.IssueDetailsMetadata;
import org.comixedproject.metadata.model.IssueMetadata;
import org.comixedproject.metadata.model.VolumeMetadata;
import org.comixedproject.model.metadata.MetadataSource;

/**
 * <code>ComicVineMetadataAdaptor</code> provides an implementation of {@link MetadataAdaptor} for
 * ComicVine.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicVineMetadataAdaptor extends AbstractMetadataAdaptor {
  static final String REFERENCE_ID_PATTERN =
      "^https?\\:\\/\\/(www\\.comicvine\\.com|comicvine\\.gamespot\\.com)\\/.*\\/4000-([\\d]+).*";
  /** The base URL for ComicVine. */
  public static final String BASE_URL = "https://comicvine.gamespot.com";

  public static final long MINIMUM_DELAY_VALUE = 1L;
  public static final int REFERENCE_ID_POSITION = 2;

  /** The action to fetch the list of volumes. */
  protected ComicVineGetVolumesAction comicVineGetVolumesAction = new ComicVineGetVolumesAction();

  /** The action to fetch all issues. */
  protected ComicVineGetAllIssuesAction comicVineGetAllIssuesAction =
      new ComicVineGetAllIssuesAction();

  /** The action to fetch a single issue. */
  protected ComicVineGetIssueAction comicVineGetIssueAction = new ComicVineGetIssueAction();

  /** The action to get the details for a single issue. */
  protected ComicVineGetIssueDetailsAction comicVineGetIssueDetailsAction =
      new ComicVineGetIssueDetailsAction();

  public ComicVineMetadataAdaptor() {
    super("ComiXed ComicVine Scraper", PROVIDER_NAME);
  }

  @Override
  public List<VolumeMetadata> getVolumes(
      final String seriesName, final Integer maxRecords, final MetadataSource metadataSource)
      throws MetadataException {
    log.debug("Fetching volumes from ComicVine: seriesName={}", seriesName);

    this.comicVineGetVolumesAction.setBaseUrl(BASE_URL);
    this.comicVineGetVolumesAction.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    this.comicVineGetVolumesAction.setDelay(this.doGetDelayValue(metadataSource));
    this.comicVineGetVolumesAction.setSeries(seriesName);
    this.comicVineGetVolumesAction.setMaxRecords(maxRecords);

    log.debug("Executing action");
    final List<VolumeMetadata> result = this.comicVineGetVolumesAction.execute();

    log.debug("Returning {} volume{}", result.size(), result.size() == 1 ? "" : "s");
    return result;
  }

  @Override
  public List<IssueDetailsMetadata> getAllIssues(
      final String volume, final MetadataSource metadataSource) throws MetadataException {
    log.debug("Fetching the list of all issues from ComicVine: volume={}", volume);

    this.comicVineGetAllIssuesAction.setBaseUrl(BASE_URL);
    this.comicVineGetAllIssuesAction.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    this.comicVineGetAllIssuesAction.setDelay(this.doGetDelayValue(metadataSource));
    this.comicVineGetAllIssuesAction.setVolumeId(volume);

    log.debug("Executing action");
    final List<IssueDetailsMetadata> result = this.comicVineGetAllIssuesAction.execute();

    log.debug("Returning {} issue{}", result.size(), result.size() == 1 ? "" : "s");
    return result;
  }

  @Override
  public IssueMetadata doGetIssue(
      final String volume, final String issueNumber, final MetadataSource metadataSource)
      throws MetadataException {
    log.debug("Fetching issue from ComicVine: volume={} issueNumber={}", volume, issueNumber);

    this.comicVineGetIssueAction.setBaseUrl(BASE_URL);
    this.comicVineGetIssueAction.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    this.comicVineGetIssueAction.setVolumeId(volume);
    this.comicVineGetIssueAction.setIssueNumber(issueNumber);

    final List<IssueMetadata> result = this.comicVineGetIssueAction.execute();

    return result.isEmpty() ? null : result.get(0);
  }

  @Override
  public IssueDetailsMetadata getIssueDetails(
      final String issueId, final MetadataSource metadataSource) throws MetadataException {
    log.debug("Fetching issue details: issueId={}", issueId);

    this.comicVineGetIssueDetailsAction.setBaseUrl(BASE_URL);
    this.comicVineGetIssueDetailsAction.setApiKey(
        this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_API_KEY, true));
    this.comicVineGetIssueDetailsAction.setIssueId(issueId);

    return this.comicVineGetIssueDetailsAction.execute();
  }

  @Override
  public String getReferenceId(final String webAddress) {
    final Pattern pattern = Pattern.compile(REFERENCE_ID_PATTERN);
    final Matcher matches = pattern.matcher(webAddress);
    String referenceId = null;
    if (matches.matches()) {
      referenceId = matches.group(REFERENCE_ID_POSITION);
    }
    return referenceId;
  }

  private long doGetDelayValue(final MetadataSource metadataSource) {
    long result = MINIMUM_DELAY_VALUE;
    try {
      final String defined =
          this.getSourcePropertyByName(metadataSource.getProperties(), PROPERTY_DELAY, false);
      if (!Objects.isNull(defined)) {
        result = Long.parseLong(defined);
      }
    } catch (MetadataException | NumberFormatException error) {
      log.error("Failed to load property: " + PROPERTY_DELAY, error);
    }
    if (result < MINIMUM_DELAY_VALUE) {
      result = MINIMUM_DELAY_VALUE;
    }
    log.trace("Returning delay value: {}", result);
    return result;
  }
}
