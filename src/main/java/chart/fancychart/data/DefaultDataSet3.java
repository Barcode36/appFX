/*
 * Copyright (C) 2014 TESIS DYNAware GmbH.
 * All rights reserved. Use is subject to license terms.
 * 
 * This file is licensed under the Eclipse Public License v1.0, which accompanies this
 * distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package chart.fancychart.data;

import java.util.List;

import model.DataItem;
import model.DataItemDao;
import model.DataItemDao.FileFormat;

public class DefaultDataSet3 {

	private static final String FILE_NAME = "random_data_3.csv";

	public static List<DataItem> getDataItems() {
		String filePath = DefaultDataSet3.class.getResource(FILE_NAME).getFile();
		return DataItemDao.importFromFile(filePath, FileFormat.CSV);
	}

}
