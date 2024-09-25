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


import com.condation.cms.api.feature.features.DBFeature;
import com.condation.cms.api.feature.features.ServerPropertiesFeature;
import com.condation.cms.api.feature.features.ThemeFeature;
import com.condation.cms.api.module.CMSModuleContext;
import com.condation.cms.api.module.CMSRequestContext;
import com.condation.modules.api.ModuleLifeCycleExtension;
import com.condation.modules.api.annotation.Extension;

/**
 *
 * @author t.marx
 */
@Extension(ModuleLifeCycleExtension.class)
public class VelocityLifecycleExtension extends ModuleLifeCycleExtension<CMSModuleContext, CMSRequestContext> {

	static VelocityTemplateEngine templateEngine;
	
	@Override
	public void init() {
	}

	@Override
	public void activate() {
		templateEngine = new VelocityTemplateEngine(
				getContext().get(DBFeature.class).db(), 
				getContext().get(ServerPropertiesFeature.class).serverProperties(), 
				getContext().get(ThemeFeature.class).theme()
		);
	}

	@Override
	public void deactivate() {
	}
}
