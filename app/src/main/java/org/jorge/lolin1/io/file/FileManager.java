package org.jorge.lolin1.io.file;

import android.support.annotation.NonNull;

import java.io.File;

/*
 * This file is part of LoLin1.
 *
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by Jorge Antonio Diaz-Benito Soriano.
 */

public abstract class FileManager {

    public static Boolean recursivelyDelete(@NonNull File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String aChildren : children) {
                Boolean success = recursivelyDelete(new File(file, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        return file.delete();
    }
}
