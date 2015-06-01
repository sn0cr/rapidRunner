/**
 * This file is part of rapidRunner <https://github.com/sn0cr/rapidRunner>
 * Copyright (C) 2015 Christian Wahl
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.sn0cr.rapidRunner.util;

/**
 * @author Christian Wahl
 *
 */
public class Pair<A, B> {
  private A       firstAttribute;
  private B secondAttribute;


  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Pair)) {
      return false;
    }
    final Pair<?, ?> other = (Pair<?, ?>) obj;
    if (this.firstAttribute == null) {
      if (other.firstAttribute != null) {
        return false;
      }
    } else if (!this.firstAttribute.equals(other.firstAttribute)) {
      return false;
    }
    if (this.secondAttribute == null) {
      if (other.secondAttribute != null) {
        return false;
      }
    } else if (!this.secondAttribute.equals(other.secondAttribute)) {
      return false;
    }
    return true;
  }

  /**
   * @return the firstAttribute
   */
  public A getA() {
    return this.firstAttribute;
  }

  /**
   * @return the secondAttribute
   */
  public B getB() {
    return this.secondAttribute;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result)
        + ((this.firstAttribute == null) ? 0 : this.firstAttribute.hashCode());
    result = (prime * result)
        + ((this.secondAttribute == null) ? 0 : this.secondAttribute.hashCode());
    return result;
  }

  /**
   * @return
   */
  public boolean notEmpty() {
    return (this.firstAttribute != null) || (this.secondAttribute != null);
  }

  /**
   * @param firstAttribute
   *          the firstAttribute to set
   */
  public final void setA(A firstAttribute) {
    this.firstAttribute = firstAttribute;
  }

  /**
   * @param secondAttribute
   *          the secondAttribute to set
   */
  public final void setB(B secondAttribute) {
    this.secondAttribute = secondAttribute;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("Pair [firstAttribute=%s, secondAttribute=%s]",
        this.firstAttribute, this.secondAttribute);
  }

}
