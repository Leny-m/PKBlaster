package com.hiveworkshop.pkb;

import com.hiveworkshop.wc3.units.objectdata.War3ID;

import javax.swing.table.AbstractTableModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UnknownChunkTableModel extends AbstractTableModel {
	private final ByteBuffer chunkData;

	public UnknownChunkTableModel(final ByteBuffer chunkData) {
		this.chunkData = chunkData;
	}

	@Override
	public String getColumnName(final int column) {
		switch (column) {
		case 0:
			return "Dec";
		case 1:
			return "Hex";
		case 2:
			return "ASCII";
		case 3:
			return "4Dec";
		case 4:
			return "4Hex";
		case 5:
			return "4ASCII";
		case 6:
			return "4Float";
		}
		return super.getColumnName(column);
	}

	@Override
	public int getRowCount() {
		return chunkData.capacity();
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return chunkData.get(rowIndex) & 0xFF;
		case 1:
			return Integer.toHexString(chunkData.get(rowIndex) & 0xFF);
		case 2:
			return (char) chunkData.get(rowIndex);
		case 3:
			if (rowIndex <= (chunkData.capacity() - 4)) {
				chunkData.order(ByteOrder.LITTLE_ENDIAN);
				return chunkData.getInt(rowIndex);
			}
			break;
		case 4:
			if (rowIndex <= (chunkData.capacity() - 4)) {
				chunkData.order(ByteOrder.LITTLE_ENDIAN);
				return Integer.toHexString(chunkData.getInt(rowIndex));
			}
			break;
		case 5:
			if (rowIndex <= (chunkData.capacity() - 4)) {
				chunkData.order(ByteOrder.LITTLE_ENDIAN);
				return new War3ID(chunkData.getInt(rowIndex));
			}
			break;
		case 6:
			if (rowIndex <= (chunkData.capacity() - 4)) {
				chunkData.order(ByteOrder.LITTLE_ENDIAN);
				return (chunkData.getFloat(rowIndex));
			}
			break;
		}
		return null;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if ((columnIndex == 0) || (columnIndex == 6)) {
			return true;
		}
		return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		if (columnIndex == 0) {
			chunkData.put(rowIndex, (byte) Integer.parseInt(aValue.toString()));
		}
		if ((columnIndex == 6) && (rowIndex <= (chunkData.capacity() - 4))) {
			chunkData.putFloat(rowIndex, Float.parseFloat(aValue.toString()));
		}
		// TODO Auto-generated method stub
		super.setValueAt(aValue, rowIndex, columnIndex);
	}

}
