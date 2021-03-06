/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.MUILabel;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.swt.util.ISWTResourceUtilities;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class BasicPartList extends AbstractTableInformationControl {

	private class BasicStackListLabelProvider extends ColumnLabelProvider {

		public String getText(Object element) {
			return ((MUILabel) element).getLabel();
		}

		public Image getImage(Object element) {
			return getLabelImage(((MUILabel) element).getIconURI());
		}

		public String getToolTipText(Object element) {
			return ((MUILabel) element).getTooltip();
		}

		public boolean useNativeToolTip(Object object) {
			return true;
		}
	}

	private Map<String, Image> images = new HashMap<String, Image>();

	private ISWTResourceUtilities utils;

	private MPartStack input;

	private EPartService partService;

	public BasicPartList(Shell parent, int shellStyle, int treeStyler, EPartService partService,
			MPartStack input, ISWTResourceUtilities utils) {
		super(parent, shellStyle, treeStyler);
		this.partService = partService;
		this.input = input;
		this.utils = utils;
	}

	private Image getLabelImage(String iconURI) {
		Image image = images.get(iconURI);
		if (image == null) {
			ImageDescriptor descriptor = utils.imageDescriptorFromURI(URI.createURI(iconURI));
			image = descriptor.createImage();
			images.put(iconURI, image);
		}
		return image;
	}

	protected TableViewer createTableViewer(Composite parent, int style) {
		Table table = new Table(parent, SWT.SINGLE | (style & ~SWT.MULTI));
		table.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
		TableViewer tableViewer = new TableViewer(table);
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.addFilter(new NamePatternFilter());
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setLabelProvider(new BasicStackListLabelProvider());

		ColumnViewerToolTipSupport.enableFor(tableViewer);
		table.addListener(SWT.Dispose, new Listener() {
			public void handleEvent(Event event) {
				for (Image image : images.values()) {
					image.dispose();
				}
			}
		});
		return tableViewer;
	}

	private List<Object> getInput() {
		List<Object> list = new ArrayList<Object>();
		for (MUIElement element : input.getChildren()) {
			if (element instanceof MPlaceholder) {
				if (!element.isToBeRendered() || !element.isVisible()) {
					continue;
				}

				element = ((MPlaceholder) element).getRef();
			}

			if (element.isToBeRendered() && element.isVisible() && element instanceof MPart) {
				list.add(element);
			}
		}
		return list;
	}

	public void setInput() {
		getTableViewer().setInput(getInput());
		selectFirstMatch();
	}

	protected void gotoSelectedElement() {
		Object selectedElement = getSelectedElement();

		// close the shell
		dispose();

		if (selectedElement instanceof MPart) {
			partService.activate((MPart) selectedElement);
		}
	}

	protected boolean deleteSelectedElements() {
		Object selectedElement = getSelectedElement();
		if (selectedElement != null) {
			partService.hidePart((MPart) selectedElement);

			if (getInput().isEmpty()) {
				getShell().dispose();
				return true;
			}
		}
		return false;

	}
}
