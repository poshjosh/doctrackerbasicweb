/*********************************************************************
*
*      Copyright (C) 2003 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package jxl.biff.drawing;

import java.util.logging.Logger;

/**
 * An Spgr container record in an escher stream
 */
class SpgrContainer extends EscherContainer
{
  /**
   * The logger
   */
  private static final Logger logger = Logger.getLogger(SpgrContainer.class.getName());

  /**
   * Constructor used when writing
   */
  public SpgrContainer()
  {
    super(EscherRecordType.SPGR_CONTAINER);
  }

  /**
   * Constructor
   *
   * @param erd the escher record data read in
   */
  public SpgrContainer(EscherRecordData erd)
  {
    super(erd);
  }
}
