package com.condation.cms.modules.velocity;

/*-
 * #%L
 * freemarker-module
 * %%
 * Copyright (C) 2024 CondationCMS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import com.condation.cms.api.ServerProperties;
import com.condation.cms.api.db.DB;
import com.condation.cms.api.template.TemplateEngine;
import com.condation.cms.api.theme.Theme;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class VelocityTemplateEngine implements TemplateEngine {

	private final VelocityEngine engine;

	private final DB db;
	private Theme theme;

	public VelocityTemplateEngine(final DB db, final ServerProperties serverProperties, final Theme theme) {

		this.db = db;
		this.theme = theme;

		try {
			engine = new VelocityEngine();

			updateTheme(theme);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public String render(final String template, final VelocityTemplateEngine.Model model) throws IOException {
		try (StringWriter out = new StringWriter()) {
			Template loadedTemplate = engine.getTemplate(template, StandardCharsets.UTF_8.name());

			VelocityContext context = new VelocityContext(model.values);

			loadedTemplate.merge(context, out);

			return out.toString();
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void invalidateCache() {
		updateTheme(theme);
	}

	@Override
	public void updateTheme(Theme theme) {
		this.theme = theme;
		Properties props = new Properties();
		props.setProperty("resource.loader", "file");
		props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");

		List<String> loaders = new ArrayList<>();
		loaders.add(db.getFileSystem().resolve("templates/").toAbsolutePath().toString());
		if (!theme.empty()) {
			loaders.add(theme.templatesPath().toAbsolutePath().toString());
			
			if (theme.getParentTheme() != null) {
				loaders.add(theme.getParentTheme().templatesPath().toAbsolutePath().toString());
			}
		}
		props.setProperty("file.resource.loader.path", String.join(",", loaders));

		props.setProperty("file.resource.loader.cache", "true");
		props.setProperty("file.resource.loader.modificationCheckInterval", "5");
		engine.reset();
		engine.init(props);

	}

}
