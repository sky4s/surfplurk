/*
 *
 * This file is part of Jupidator.
 *
 * Jupidator is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2.
 *
 *
 * Jupidator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jupidator; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package com.panayotis.jupidator.loglist;

import java.io.Serializable;

/**
 *
 * @author teras
 */
public class LogItem implements Serializable {

    private String version;
    private String info;

    public LogItem(String version, String info) {
        this.version = version;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getVersion() {
        return version;
    }
}
