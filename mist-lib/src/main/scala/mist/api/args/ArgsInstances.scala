package mist.api.args

import mist.api.FnContext

trait ArgsInstances {

  class NamedUserArg[A](name: String)(implicit pe: ArgExtractor[A]) extends UserArg[A] {
    override def describe(): Seq[ArgInfo] = Seq(UserInputArgument(name, pe.`type`))

    override def extract(ctx: FnContext): ArgExtraction[A] = {
      pe.extract(ctx.params.getOrElse(name, null))
    }
  }

  class NamedUserArgWithDefault[A](name: String, default: A)(implicit pe: ArgExtractor[A]) extends UserArg[A] {
    override def describe(): Seq[ArgInfo] = Seq(UserInputArgument(name, MOption(pe.`type`)))

    override def extract(ctx: FnContext): ArgExtraction[A] = {
      ctx.params.get(name) match {
        case Some(a) => pe.extract(a)
        case _ => Extracted(default)
      }
    }
  }

  def arg[A](name: String)(implicit pe: ArgExtractor[A]): UserArg[A] =
    new NamedUserArg[A](name)

  def arg[A](name: String, default: A)(implicit pe: ArgExtractor[A]): UserArg[A] =
    new NamedUserArgWithDefault[A](name, default)

  def arg[A](implicit le: RootExtractor[A]): UserArg[A] = {
    new UserArg[A] {
      override def describe(): Seq[ArgInfo] = {
        le.`type` match {
          case MObj(fields) => fields.map({case (k, v) => UserInputArgument(k, v)})
          case MMap(k, v) => Seq()
        }
      }

      override def extract(ctx: FnContext) = le.extract(ctx.params)
    }
  }

  val allArgs: ArgDef[Map[String, Any]] = new SystemArg[Map[String, Any]] {
    override def extract(ctx: FnContext): ArgExtraction[Map[String, Any]] = Extracted(ctx.params)

    override def describe(): Seq[ArgInfo] = Seq(InternalArgument())
  }
}

object ArgsInstances extends ArgsInstances