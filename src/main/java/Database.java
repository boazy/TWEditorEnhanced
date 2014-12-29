package TWEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Database
{
  private File file;
  private String name;
  private String fileType;
  private String fileVersion;
  private DBElement topLevelStruct;
  private byte[] structBuffer;
  private int structArraySize;
  private int structArrayCount;
  private byte[] fieldBuffer;
  private int fieldArraySize;
  private int fieldArrayCount;
  private byte[] labelBuffer;
  private int labelArraySize;
  private int labelArrayCount;
  private byte[] fieldDataBuffer;
  private int fieldDataSize;
  private int fieldDataLength;
  private byte[] fieldIndicesBuffer;
  private int fieldIndicesSize;
  private int fieldIndicesLength;
  private byte[] listIndicesBuffer;
  private int listIndicesSize;
  private int listIndicesLength;

  public Database()
  {
    this.name = new String();
  }

  public Database(String filePath)
  {
    this(new File(filePath));
  }

  public Database(File file)
  {
    this.file = file;
    this.name = file.getName();
  }

  public File getFile()
  {
    return this.file;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getType()
  {
    return this.fileType;
  }

  public void setType(String type)
  {
    if (type.length() != 4) {
      throw new IllegalArgumentException("The file type is not 4 characters");
    }
    this.fileType = type;
  }

  public String getVersion()
  {
    return this.fileVersion;
  }

  public void setVersion(String version)
  {
    if ((!version.equals("V3.2")) && (!version.equals("V3.3"))) {
      throw new IllegalArgumentException("File version " + version + " is not supported");
    }
    this.fileVersion = version;
  }

  public DBElement getTopLevelStruct()
  {
    return this.topLevelStruct;
  }

  public void setTopLevelStruct(DBElement struct)
  {
    if (struct.getType() != 14) {
      throw new IllegalArgumentException("Database element is not a structure");
    }
    this.topLevelStruct = struct;
  }

  public void load()
    throws DBException, IOException
  {
    if (this.file == null) {
      throw new IllegalStateException("No database file is available");
    }
    FileInputStream in = new FileInputStream(this.file);
    try {
      load(in);
    } finally {
      in.close();
    }
  }

  public void load(InputStream in)
    throws DBException, IOException
  {
    try
    {
      byte[] headerBuffer = new byte[56];
      int count = in.read(headerBuffer);
      if (count != 56) {
        throw new DBException(this.name + ": GFF header is too short");
      }

      this.fileType = new String(headerBuffer, 0, 4);
      this.fileVersion = new String(headerBuffer, 4, 4);
      if ((!this.fileVersion.equals("V3.2")) && (!this.fileVersion.equals("V3.3"))) {
        throw new DBException(this.name + ": GFF version " + this.fileVersion + " is not supported");
      }
      int structBaseOffset = getInteger(headerBuffer, 8);
      this.structArrayCount = getInteger(headerBuffer, 12);
      this.structArraySize = this.structArrayCount;
      int fieldBaseOffset = getInteger(headerBuffer, 16);
      this.fieldArrayCount = getInteger(headerBuffer, 20);
      this.fieldArraySize = this.fieldArrayCount;
      int labelBaseOffset = getInteger(headerBuffer, 24);
      this.labelArrayCount = getInteger(headerBuffer, 28);
      this.labelArraySize = this.labelArrayCount;
      int fieldDataOffset = getInteger(headerBuffer, 32);
      this.fieldDataLength = getInteger(headerBuffer, 36);
      this.fieldDataSize = this.fieldDataLength;
      int fieldIndicesOffset = getInteger(headerBuffer, 40);
      this.fieldIndicesLength = getInteger(headerBuffer, 44);
      this.fieldIndicesSize = this.fieldIndicesLength;
      int listIndicesOffset = getInteger(headerBuffer, 48);
      this.listIndicesLength = getInteger(headerBuffer, 52);
      this.listIndicesSize = this.listIndicesLength;

      if (this.structArrayCount < 1) {
        throw new DBException(this.name + ": GFF file contains no structures");
      }
      int size = 12 * this.structArraySize;
      this.structBuffer = new byte[size];
      count = in.read(this.structBuffer);
      if (count != size) {
        throw new DBException(this.name + ": Structure array data truncated");
      }

      if (this.fieldArrayCount > 0) {
        size = 12 * this.fieldArraySize;
        this.fieldBuffer = new byte[size];
        count = in.read(this.fieldBuffer);
        if (count != size) {
          throw new DBException(this.name + ": Field array data truncated");
        }

      }

      if (this.labelArrayCount > 0) {
        size = 16 * this.labelArraySize;
        this.labelBuffer = new byte[size];
        count = in.read(this.labelBuffer);
        if (count != size) {
          throw new DBException(this.name + ": Label array data truncated");
        }

      }

      if (this.fieldDataLength > 0) {
        this.fieldDataBuffer = new byte[this.fieldDataSize];
        count = in.read(this.fieldDataBuffer);
        if (count != this.fieldDataSize) {
          throw new DBException(this.name + ": Field data truncated");
        }

      }

      if (this.fieldIndicesLength > 0) {
        this.fieldIndicesBuffer = new byte[this.fieldIndicesSize];
        count = in.read(this.fieldIndicesBuffer);
        if (count != this.fieldIndicesSize) {
          throw new DBException(this.name + ": Field indices truncated");
        }

      }

      if (this.listIndicesLength > 0) {
        this.listIndicesBuffer = new byte[this.listIndicesSize];
        count = in.read(this.listIndicesBuffer);
        if (count != this.listIndicesSize) {
          throw new DBException(this.name + ": List indices truncated");
        }

      }

      this.topLevelStruct = decodeStruct(new String(), 0);
    }
    finally
    {
      this.structBuffer = null;
      this.fieldBuffer = null;
      this.labelBuffer = null;
      this.fieldDataBuffer = null;
      this.fieldIndicesBuffer = null;
      this.listIndicesBuffer = null;
    }
  }

  private DBElement decodeField(int index)
    throws DBException
  {
    if (index >= this.fieldArrayCount) {
      throw new DBException(this.name + ": Field index " + index + " exceeds array size");
    }

    int offset = 12 * index;
    int fieldType = getInteger(this.fieldBuffer, offset);
    int labelIndex = getInteger(this.fieldBuffer, offset + 4);
    int dataOffset = getInteger(this.fieldBuffer, offset + 8);
    if (labelIndex >= this.labelArrayCount) {
      throw new DBException(this.name + ": Label index " + labelIndex + " exceeds array size");
    }

    int labelOffset = 16 * labelIndex;
    int labelLength;
    for (labelLength = 16; (labelLength > 0) &&
      (this.labelBuffer[(labelOffset + labelLength - 1)] == 0); labelLength--);
    String label = new String(this.labelBuffer, labelOffset, labelLength);
    DBElement element;
    switch (fieldType) {
    case 15:
      element = decodeList(label, dataOffset);
      break;
    case 14:
      element = decodeStruct(label, dataOffset);
      break;
    case 0:
      element = new DBElement(fieldType, 0, label, new Integer(dataOffset & 0xFF));
      break;
    case 1:
      element = new DBElement(fieldType, 0, label, new Character((char)dataOffset));
      break;
    case 2:
      element = new DBElement(fieldType, 0, label, new Integer(dataOffset & 0xFFFF));
      break;
    case 3:
      dataOffset &= 65535;
      if (dataOffset > 32767)
        dataOffset |= -65536;
      element = new DBElement(fieldType, 0, label, new Integer(dataOffset));
      break;
    case 4:
      element = new DBElement(fieldType, 0, label, new Long(dataOffset & 0xFFFFFFFF));
      break;
    case 5:
      element = new DBElement(fieldType, 0, label, new Integer(dataOffset));
      break;
    case 6:
    case 7:
      if (dataOffset + 8 > this.fieldDataLength) {
        throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
      }
      long longValue = this.fieldDataBuffer[(dataOffset + 0)] & 0xFF | (this.fieldDataBuffer[(dataOffset + 1)] & 0xFF) << 8 | (this.fieldDataBuffer[(dataOffset + 2)] & 0xFF) << 16 | (this.fieldDataBuffer[(dataOffset + 3)] & 0xFF) << 24 | (this.fieldDataBuffer[(dataOffset + 4)] & 0xFF) << 32 | (this.fieldDataBuffer[(dataOffset + 5)] & 0xFF) << 40 | (this.fieldDataBuffer[(dataOffset + 6)] & 0xFF) << 48 | (this.fieldDataBuffer[(dataOffset + 7)] & 0xFF) << 56;

      if ((fieldType == 6) && (longValue < 0L)) {
        throw new DBException("DWORD64 value is too large for Java representation");
      }
      element = new DBElement(fieldType, 0, label, new Long(longValue));
      break;
    case 8:
      element = new DBElement(fieldType, 0, label, new Float(Float.intBitsToFloat(dataOffset)));
      break;
    case 9:
      if (dataOffset + 8 > this.fieldDataLength) {
        throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
      }
      long longBits = this.fieldDataBuffer[(dataOffset + 0)] & 0xFF | (this.fieldDataBuffer[(dataOffset + 1)] & 0xFF) << 8 | (this.fieldDataBuffer[(dataOffset + 2)] & 0xFF) << 16 | (this.fieldDataBuffer[(dataOffset + 3)] & 0xFF) << 24 | (this.fieldDataBuffer[(dataOffset + 4)] & 0xFF) << 32 | (this.fieldDataBuffer[(dataOffset + 5)] & 0xFF) << 40 | (this.fieldDataBuffer[(dataOffset + 6)] & 0xFF) << 48 | (this.fieldDataBuffer[(dataOffset + 7)] & 0xFF) << 56;

      element = new DBElement(fieldType, 0, label, new Double(Double.longBitsToDouble(longBits)));
      break;
    case 13:
      if (dataOffset + 4 > this.fieldDataLength) {
        throw new DBException("Field data offset " + dataOffset + " exceeds field data");
      }
      int byteLength = getInteger(this.fieldDataBuffer, dataOffset);
      dataOffset += 4;
      if (dataOffset + byteLength > this.fieldDataLength) {
        throw new DBException("Void data length " + byteLength + " exceeds field data");
      }
      byte[] byteData = new byte[byteLength];
      if (byteLength > 0) {
        System.arraycopy(this.fieldDataBuffer, dataOffset, byteData, 0, byteLength);
      }
      element = new DBElement(fieldType, 0, label, byteData);
      break;
    case 11:
      if (dataOffset + 1 > this.fieldDataLength) {
        throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
      }
      int resourceLength = this.fieldDataBuffer[dataOffset] & 0xFF;
      dataOffset++;
      if (dataOffset + resourceLength > this.fieldDataLength)
        throw new DBException(this.name + ": Resource length " + resourceLength + " exceeds field data");
      String resourceString;
      if (resourceLength > 0)
        try {
          resourceString = new String(this.fieldDataBuffer, dataOffset, resourceLength, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
          throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
        }
      else {
        resourceString = new String();
      }

      element = new DBElement(fieldType, 0, label, resourceString);
      break;
    case 10:
      if (dataOffset + 4 > this.fieldDataLength) {
        throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
      }
      int stringLength = getInteger(this.fieldDataBuffer, dataOffset);
      dataOffset += 4;
      if (dataOffset + stringLength > this.fieldDataLength)
        throw new DBException(this.name + ": String length " + stringLength + " exceeds field data");
      String string;
      if (stringLength > 0)
        try {
          string = new String(this.fieldDataBuffer, dataOffset, stringLength, "UTF-8");
        } catch (UnsupportedEncodingException exc) {
          throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
        }
      else {
        string = new String();
      }

      element = new DBElement(fieldType, 0, label, string);
      break;
    case 12:
      if (dataOffset + 12 > this.fieldDataLength) {
        throw new DBException(this.name + ": Field data offset " + dataOffset + " exceeds field data");
      }
      int localizedLength = getInteger(this.fieldDataBuffer, dataOffset);
      int stringReference = getInteger(this.fieldDataBuffer, dataOffset + 4);
      int substringCount = getInteger(this.fieldDataBuffer, dataOffset + 8);
      dataOffset += 12;
      localizedLength -= 8;
      LocalizedString localizedString = new LocalizedString(stringReference);

      for (int i = 0; i < substringCount; i++) {
        if (dataOffset + 8 > this.fieldDataLength) {
          throw new DBException(this.name + ": Localized substring " + i + " exceeds field data");
        }
        if (localizedLength < 8) {
          throw new DBException(this.name + ": Localized substring " + i + " exceeds localized string");
        }
        int stringID = getInteger(this.fieldDataBuffer, dataOffset);
        int substringLength = getInteger(this.fieldDataBuffer, dataOffset + 4);
        dataOffset += 8;
        localizedLength -= 8;
        if (dataOffset + substringLength > this.fieldDataLength) {
          throw new DBException(this.name + ": Localized substring " + i + " exceeds field data");
        }
        if (substringLength > localizedLength)
          throw new DBException(this.name + ": Localized substring " + i + " exceeds localized string");
        String substring;
        if (substringLength > 0)
          try {
            substring = new String(this.fieldDataBuffer, dataOffset, substringLength, "UTF-8");
          } catch (UnsupportedEncodingException exc) {
            throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
          }
        else {
          substring = new String();
        }

        localizedString.addSubstring(new LocalizedSubstring(substring, stringID / 2, stringID & 0x1));
        dataOffset += substringLength;
        localizedLength -= substringLength;
      }

      element = new DBElement(fieldType, 0, label, localizedString);
      break;
    default:
      throw new DBException(this.name + ": Unrecognized field type " + fieldType);
    }

    return element;
  }

  private DBElement decodeStruct(String label, int index)
    throws DBException
  {
    if (index >= this.structArrayCount) {
      throw new DBException(this.name + ": Structure index " + index + " exceeds array size");
    }

    int offset = 12 * index;
    int id = getInteger(this.structBuffer, offset);
    int fieldIndex = getInteger(this.structBuffer, offset + 4);
    int fieldCount = getInteger(this.structBuffer, offset + 8);
    DBList list = new DBList(fieldCount);
    if (fieldCount == 1) {
      DBElement field = decodeField(fieldIndex);
      list.addElement(field);
    } else if (fieldCount > 1) {
      offset = fieldIndex;
      for (int i = 0; i < fieldCount; i++) {
        if (offset + 4 > this.fieldIndicesLength) {
          throw new DBException("Field indices offset " + offset + " exceeds indices size");
        }
        fieldIndex = getInteger(this.fieldIndicesBuffer, offset);
        offset += 4;
        DBElement field = decodeField(fieldIndex);
        list.addElement(field);
      }
    }

    return new DBElement(14, id, label, list);
  }

  private DBElement decodeList(String label, int offset)
    throws DBException
  {
    if (offset + 4 > this.listIndicesLength) {
      throw new DBException(this.name + ": List indices offset " + offset + " exceeds indices size");
    }

    int structCount = getInteger(this.listIndicesBuffer, offset);
    DBList list = new DBList(structCount);
    int listOffset = offset + 4;
    for (int i = 0; i < structCount; i++) {
      if (listOffset + 4 > this.listIndicesLength) {
        throw new DBException(this.name + ": List indices offset " + listOffset + " exceeds indices size");
      }
      int structIndex = getInteger(this.listIndicesBuffer, listOffset);
      listOffset += 4;
      list.addElement(decodeStruct(new String(), structIndex));
    }

    return new DBElement(15, 0, label, list);
  }

  public void save()
    throws DBException, IOException
  {
    File tmpFile = null;
    FileOutputStream out = null;
    if (this.file == null) {
      throw new IllegalStateException("No database file is available");
    }

    try
    {
      tmpFile = new File(this.file.getPath() + ".new");
      out = new FileOutputStream(tmpFile);
      save(out);
      out.close();
      out = null;

      if ((this.file.exists()) && 
        (!this.file.delete())) {
        throw new IOException("Unable to delete " + this.file.getName());
      }
      if (!tmpFile.renameTo(this.file)) {
        throw new IOException("Unable to rename " + tmpFile.getName() + " to " + this.file.getName());
      }

    }
    finally
    {
      if (tmpFile != null) {
        if (out != null) {
          out.close();
        }
        if (tmpFile.exists())
          tmpFile.delete();
      }
    }
  }

  public void save(OutputStream out)
    throws DBException, IOException
  {
    try
    {
      this.structBuffer = new byte[48000];
      this.structArraySize = 4000;
      this.structArrayCount = 0;

      this.fieldBuffer = new byte[144000];
      this.fieldArraySize = 12000;
      this.fieldArrayCount = 0;

      this.labelBuffer = new byte[16000];
      this.labelArraySize = 1000;
      this.labelArrayCount = 0;

      this.fieldDataBuffer = new byte[20000];
      this.fieldDataSize = 20000;
      this.fieldDataLength = 0;

      this.fieldIndicesBuffer = new byte[36000];
      this.fieldIndicesSize = 36000;
      this.fieldIndicesLength = 0;

      this.listIndicesBuffer = new byte[8000];
      this.listIndicesSize = 8000;
      this.listIndicesLength = 0;

      if (this.topLevelStruct == null) {
        throw new DBException(this.name + ": No top-level structure");
      }
      if ((this.fileType == null) || (this.fileType.length() != 4)) {
        throw new DBException(this.name + ": File type is not set");
      }
      if ((this.fileVersion == null) || (this.fileVersion.length() != 4)) {
        throw new DBException(this.name + ": File version is not set");
      }
      encodeStruct(this.topLevelStruct);

      byte[] headerBuffer = new byte[56];
      byte[] buffer = this.fileType.getBytes();
      System.arraycopy(buffer, 0, headerBuffer, 0, 4);
      buffer = this.fileVersion.getBytes();
      System.arraycopy(buffer, 0, headerBuffer, 4, 4);
      int offset = 56;
      int structLength = 12 * this.structArrayCount;
      setInteger(offset, headerBuffer, 8);
      setInteger(this.structArrayCount, headerBuffer, 12);
      offset += structLength;
      int fieldLength = 12 * this.fieldArrayCount;
      setInteger(offset, headerBuffer, 16);
      setInteger(this.fieldArrayCount, headerBuffer, 20);
      offset += fieldLength;
      int labelLength = 16 * this.labelArrayCount;
      setInteger(offset, headerBuffer, 24);
      setInteger(this.labelArrayCount, headerBuffer, 28);
      offset += labelLength;
      setInteger(offset, headerBuffer, 32);
      setInteger(this.fieldDataLength, headerBuffer, 36);
      offset += this.fieldDataLength;
      setInteger(offset, headerBuffer, 40);
      setInteger(this.fieldIndicesLength, headerBuffer, 44);
      offset += this.fieldIndicesLength;
      setInteger(offset, headerBuffer, 48);
      setInteger(this.listIndicesLength, headerBuffer, 52);

      out.write(headerBuffer);
      out.write(this.structBuffer, 0, structLength);
      if (fieldLength != 0)
        out.write(this.fieldBuffer, 0, fieldLength);
      if (labelLength != 0)
        out.write(this.labelBuffer, 0, labelLength);
      if (this.fieldDataLength != 0)
        out.write(this.fieldDataBuffer, 0, this.fieldDataLength);
      if (this.fieldIndicesLength != 0)
        out.write(this.fieldIndicesBuffer, 0, this.fieldIndicesLength);
      if (this.listIndicesLength != 0) {
        out.write(this.listIndicesBuffer, 0, this.listIndicesLength);
      }

    }
    finally
    {
      this.structBuffer = null;
      this.fieldBuffer = null;
      this.labelBuffer = null;
      this.fieldDataBuffer = null;
      this.fieldIndicesBuffer = null;
      this.listIndicesBuffer = null;
    }
  }

  private int encodeField(DBElement element)
    throws DBException
  {
    int fieldType = element.getType();

    String fieldLabel = element.getLabel();
    if (fieldLabel.length() == 0) {
      throw new DBException("Field does not have a label");
    }
    byte[] labelBytes = fieldLabel.getBytes();
    byte[] label = new byte[16];
    boolean match = false;
    System.arraycopy(labelBytes, 0, label, 0, Math.min(labelBytes.length, 16));
    int labelIndex;
    for (labelIndex = 0; labelIndex < this.labelArrayCount; labelIndex++) {
      int labelOffset = labelIndex * 16;
      match = true;
      for (int i = 0; i < 16; i++) {
        if (this.labelBuffer[(labelOffset + i)] != label[i]) {
          match = false;
          break;
        }
      }

      if (match) {
        break;
      }
    }
    if (!match) {
      if (this.labelArrayCount == this.labelArraySize) {
        this.labelArraySize += 1000;
        byte[] buffer = new byte[16 * this.labelArraySize];
        System.arraycopy(this.labelBuffer, 0, buffer, 0, this.labelArrayCount * 16);
        this.labelBuffer = buffer;
      }

      labelIndex = this.labelArrayCount++;
      int labelOffset = labelIndex * 16;
      System.arraycopy(label, 0, this.labelBuffer, labelOffset, 16);
    }

    Object fieldValue = element.getValue();
    int dataOffset;
    switch (fieldType) {
    case 15:
      dataOffset = encodeList(element);
      break;
    case 14:
      dataOffset = encodeStruct(element);
      break;
    case 0:
      dataOffset = ((Integer)fieldValue).intValue() & 0xFF;
      break;
    case 1:
      dataOffset = ((Character)fieldValue).charValue() & 0xFFFF;
      break;
    case 2:
    case 3:
      dataOffset = ((Integer)fieldValue).intValue() & 0xFFFF;
      break;
    case 4:
      dataOffset = ((Long)fieldValue).intValue();
      break;
    case 5:
      dataOffset = ((Integer)fieldValue).intValue();
      break;
    case 6:
    case 7:
      dataOffset = setFieldData(((Long)fieldValue).longValue());
      break;
    case 8:
      dataOffset = Float.floatToIntBits(((Float)fieldValue).floatValue());
      break;
    case 9:
      dataOffset = setFieldData(Double.doubleToLongBits(((Double)fieldValue).doubleValue()));
      break;
    case 13:
      byte[] voidData = (byte[])fieldValue;
      int voidLength = voidData.length;
      byte[] voidBuffer = new byte[4 + voidLength];
      setInteger(voidLength, voidBuffer, 0);
      System.arraycopy(voidData, 0, voidBuffer, 4, voidLength);
      dataOffset = setFieldData(voidBuffer);
      break;
    case 11:
      String resourceString = (String)fieldValue;
      byte[] resourceData;
      try
      {
        resourceData = resourceString.getBytes("UTF-8");
      } catch (UnsupportedEncodingException exc) {
        throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
      }

      int resourceLength = resourceData.length;
      if (resourceLength > 255) {
        throw new DBException("Resource length is greater than 255");
      }
      byte[] resourceBuffer = new byte[1 + resourceLength];
      resourceBuffer[0] = ((byte)resourceLength);
      System.arraycopy(resourceData, 0, resourceBuffer, 1, resourceLength);
      dataOffset = setFieldData(resourceBuffer);
      break;
    case 10:
      String string = (String)fieldValue;
      byte[] stringBuffer;
      if (string.length() > 0) {
        byte[] stringData;
        try {
          stringData = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException exc) {
          throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
        }

        int stringLength = stringData.length;
        stringBuffer = new byte[4 + stringLength];
        setInteger(stringLength, stringBuffer, 0);
        System.arraycopy(stringData, 0, stringBuffer, 4, stringLength);
      } else {
        stringBuffer = new byte[4];
        setInteger(0, stringBuffer, 0);
      }

      dataOffset = setFieldData(stringBuffer);
      break;
    case 12:
      LocalizedString localizedString = (LocalizedString)fieldValue;
      int substringCount = localizedString.getSubstringCount();
      int localizedLength = 8;
      List substringList = new ArrayList(substringCount);

      for (int i = 0; i < substringCount; i++) {
        LocalizedSubstring localizedSubstring = localizedString.getSubstring(i);
        String substring = localizedSubstring.getString();
        byte[] substringData;
        if (substring.length() > 0)
          try {
            substringData = substring.getBytes("UTF-8");
          } catch (UnsupportedEncodingException exc) {
            throw new DBException(this.name + ": UTF-8 encoding is not supported", exc);
          }
        else {
          substringData = new byte[0];
        }

        substringList.add(substringData);
        localizedLength += 8 + substringData.length;
      }

      byte[] localizedBuffer = new byte[4 + localizedLength];
      setInteger(localizedLength, localizedBuffer, 0);
      setInteger(localizedString.getStringReference(), localizedBuffer, 4);
      setInteger(substringCount, localizedBuffer, 8);
      int substringOffset = 12;

      for (int i = 0; i < substringCount; i++) {
        LocalizedSubstring localizedSubstring = localizedString.getSubstring(i);
        byte[] substringData = (byte[])substringList.get(i);
        int substringLength = substringData.length;
        setInteger(localizedSubstring.getLanguage() * 2 + localizedSubstring.getGender(), localizedBuffer, substringOffset);

        setInteger(substringLength, localizedBuffer, substringOffset + 4);
        if (substringLength > 0)
          System.arraycopy(substringData, 0, localizedBuffer, substringOffset + 8, substringLength);
        substringOffset += 8 + substringLength;
      }

      dataOffset = setFieldData(localizedBuffer);
      break;
    default:
      throw new DBException(this.name + ": Unrecognized field type " + fieldType);
    }

    if (this.fieldArrayCount == this.fieldArraySize) {
      this.fieldArraySize += 4000;
      byte[] buffer = new byte[12 * this.fieldArraySize];
      System.arraycopy(this.fieldBuffer, 0, buffer, 0, this.fieldArrayCount * 12);
      this.fieldBuffer = buffer;
    }

    int fieldIndex = this.fieldArrayCount++;
    int fieldOffset = fieldIndex * 12;
    setInteger(fieldType, this.fieldBuffer, fieldOffset);
    setInteger(labelIndex, this.fieldBuffer, fieldOffset + 4);
    setInteger(dataOffset, this.fieldBuffer, fieldOffset + 8);
    return fieldIndex;
  }

  private int encodeStruct(DBElement element)
    throws DBException
  {
    DBList list = (DBList)element.getValue();
    int fieldCount = list.getElementCount();
    int fieldOffset = 0;

    if (this.structArrayCount == this.structArraySize) {
      this.structArraySize += 2000;
      byte[] buffer = new byte[12 * this.structArraySize];
      System.arraycopy(this.structBuffer, 0, buffer, 0, this.structArrayCount * 12);
      this.structBuffer = buffer;
    }

    int structIndex = this.structArrayCount++;

    if (fieldCount == 1) {
      fieldOffset = encodeField(list.getElement(0));
    } else if (fieldCount > 1) {
      int indexLength = 4 * fieldCount;
      if (this.fieldIndicesLength + indexLength > this.fieldIndicesSize) {
        int increment = Math.max(indexLength, 8000);
        this.fieldIndicesSize += increment;
        byte[] buffer = new byte[this.fieldIndicesSize];
        System.arraycopy(this.fieldIndicesBuffer, 0, buffer, 0, this.fieldIndicesLength);
        this.fieldIndicesBuffer = buffer;
      }

      fieldOffset = this.fieldIndicesLength;
      this.fieldIndicesLength += indexLength;
      for (int i = 0; i < fieldCount; i++) {
        int fieldIndex = encodeField(list.getElement(i));
        setInteger(fieldIndex, this.fieldIndicesBuffer, fieldOffset + 4 * i);
      }

    }

    int structOffset = structIndex * 12;
    setInteger(element.getID(), this.structBuffer, structOffset);
    setInteger(fieldOffset, this.structBuffer, structOffset + 4);
    setInteger(fieldCount, this.structBuffer, structOffset + 8);
    return structIndex;
  }

  private int encodeList(DBElement element)
    throws DBException
  {
    DBList list = (DBList)element.getValue();
    int listCount = list.getElementCount();
    int listLength = (listCount + 1) * 4;
    if (this.listIndicesLength + listLength > this.listIndicesSize) {
      int increment = Math.max(listLength, 2000);
      this.listIndicesSize += increment;
      byte[] buffer = new byte[this.listIndicesSize];
      System.arraycopy(this.listIndicesBuffer, 0, buffer, 0, this.listIndicesLength);
      this.listIndicesBuffer = buffer;
    }

    int listOffset = this.listIndicesLength;
    this.listIndicesLength += listLength;
    setInteger(listCount, this.listIndicesBuffer, listOffset);

    for (int i = 0; i < listCount; i++) {
      int structIndex = encodeStruct(list.getElement(i));
      setInteger(structIndex, this.listIndicesBuffer, listOffset + 4 * (i + 1));
    }

    return listOffset;
  }

  private int setFieldData(byte[] data)
  {
    int dataLength = data.length;
    if (this.fieldDataLength + dataLength > this.fieldDataSize) {
      int increment = Math.max(dataLength, 8000);
      this.fieldDataSize += increment;
      byte[] buffer = new byte[this.fieldDataSize];
      System.arraycopy(this.fieldDataBuffer, 0, buffer, 0, this.fieldDataLength);
      this.fieldDataBuffer = buffer;
    }

    int dataOffset = this.fieldDataLength;
    this.fieldDataLength += dataLength;
    System.arraycopy(data, 0, this.fieldDataBuffer, dataOffset, dataLength);
    return dataOffset;
  }

  private int setFieldData(long data)
  {
    if (this.fieldDataLength + 8 > this.fieldDataSize) {
      this.fieldDataSize += 8000;
      byte[] buffer = new byte[this.fieldDataSize];
      System.arraycopy(this.fieldDataBuffer, 0, buffer, 0, this.fieldDataLength);
      this.fieldDataBuffer = buffer;
    }

    int dataOffset = this.fieldDataLength;
    this.fieldDataLength += 8;
    this.fieldDataBuffer[(dataOffset + 0)] = ((byte)(int)data);
    this.fieldDataBuffer[(dataOffset + 1)] = ((byte)(int)(data >> 8));
    this.fieldDataBuffer[(dataOffset + 2)] = ((byte)(int)(data >> 16));
    this.fieldDataBuffer[(dataOffset + 3)] = ((byte)(int)(data >> 24));
    this.fieldDataBuffer[(dataOffset + 4)] = ((byte)(int)(data >> 32));
    this.fieldDataBuffer[(dataOffset + 5)] = ((byte)(int)(data >> 40));
    this.fieldDataBuffer[(dataOffset + 6)] = ((byte)(int)(data >> 48));
    this.fieldDataBuffer[(dataOffset + 7)] = ((byte)(int)(data >> 56));
    return dataOffset;
  }

  private int getInteger(byte[] buffer, int offset)
  {
    return buffer[(offset + 0)] & 0xFF | (buffer[(offset + 1)] & 0xFF) << 8 | (buffer[(offset + 2)] & 0xFF) << 16 | (buffer[(offset + 3)] & 0xFF) << 24;
  }

  private void setInteger(int number, byte[] buffer, int offset)
  {
    buffer[offset] = ((byte)number);
    buffer[(offset + 1)] = ((byte)(number >>> 8));
    buffer[(offset + 2)] = ((byte)(number >>> 16));
    buffer[(offset + 3)] = ((byte)(number >>> 24));
  }
}

