/*
 * Copyright 2010 Kevin Hunter
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.sourceforge.wsup.hibernate;

/**
 * This interface allows a {@link Command} to provide an indications of
 * exceptions that should result in a transaction being retried.
 * {@link net.sourceforge.wsup.hibernate.impl.ContextCommandRunnerImpl} provides an overridable method
 * {@link net.sourceforge.wsup.hibernate.impl.ContextCommandRunnerImpl#isRetryable} that will handle global cases
 * (such as database connectivity problems). Providing this interface on a
 * <code>Command</code> is typically done to handle special cases.
 * <p>
 * As one potential example, suppose a table has a column that (a) is supposed
 * to contain a unique integer (enforced by a <code>unique</code> constraint)
 * and (b) the value for this column is calculated at runtime, say by taking the
 * maximum value already in the column and incrementing it. In such a case, it
 * is possible for two update attempts to "collide," resulting in a constraint
 * violation exception for one of them. Under this situation, the command
 * should probably be retried. Normally, a constraint violation won't be
 * retried, since it typically indicates a programming error. This is a special
 * situation, however, and thus the <code>Command</code> in question could
 * implement this interface and return true for instances of
 * <code>ConstraintViolationException</code>.
 * </p>
 * 
 * @author Kevin Hunter
 * 
 */
public interface RetryableExceptions
{
	public boolean isRetryable(RuntimeException exception);
}
