/*
 * Copyright (c) 2009, SQL Power Group Inc.
 *
 * This file is part of Power*Architect.
 *
 * Power*Architect is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Power*Architect is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */

package ca.sqlpower.architect.ddl.critic;

import java.util.List;

/**
 * A critic is used by a {@link Criticizer} to find mistakes in a project.
 * The critic does the actual work of analyzing an object or structure of
 * the project and highlights errors by creating {@link Criticism}s.
 * 
 * @param <S> The object type that will be analyzed to be criticized.
 */
public interface Critic<S> {

    public List<Criticism<S>> criticize(S subject);
}