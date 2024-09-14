/*
 * ComiXed - A digital comic book library management application.
 * Copyright (C) 2023, The ComiXed Project
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

import lombok.extern.log4j.Log4j2;
import org.comixedproject.metadata.AbstractMetadataAdaptorProvider;
import org.comixedproject.metadata.MetadataAdaptorProvider;
import org.comixedproject.metadata.adaptors.MetadataAdaptor;

/**
 * <code>ComicVineMetadataAdaptorProvider</code> defines a {@link MetadataAdaptorProvider} for the
 * ComicVine service.
 *
 * @author Darryl L. Pierce
 */
@Log4j2
public class ComicVineMetadataAdaptorProvider extends AbstractMetadataAdaptorProvider {
  /** The adaptor name. */
  public static final String PROVIDER_NAME = "ComicVineMetadataAdaptor";

  /** The API key property name. */
  static final String PROPERTY_API_KEY = "comic-vine.api-key";

  private static final String VERSION = "2.1.2-1";
  private static final String HOMEPAGE = "http://www.github.com/comixed/comixed-metadata-comicvine";
  static final String PROPERTY_DELAY = "comic-vine.delay";

  /** Creates a default instance. */
  public ComicVineMetadataAdaptorProvider() {
    super(PROVIDER_NAME, VERSION, HOMEPAGE);

    this.addProperty(PROPERTY_API_KEY);
    this.addProperty(PROPERTY_DELAY);
  }

  @Override
  public MetadataAdaptor create() {
    log.debug("Creating an instance of the ComicVine metadata adaptor");
    return new ComicVineMetadataAdaptor();
  }

  @Override
  public boolean supportedReference(final String reference) {
    return reference.matches(ComicVineMetadataAdaptor.REFERENCE_ID_PATTERN);
  }
}
