package native
package nir

import native.util.{sh, Show}, Show.{Sequence => s, Indent => i, Unindent => ui,
                                     Repeat => r, Newline => nl}

object Shows {
  implicit val showAttrs: Show[Seq[Attr]] = Show { attrs =>
    if (attrs.isEmpty) s()
    else r(attrs, sep = " ", post = " ")
  }

  implicit val showAttr: Show[Attr] = Show {
    case Attr.Usgn => "usgn"

    case Attr.NoInline     => "noinline"
    case Attr.AlwaysInline => "alwaysinline"
    case Attr.InlineHint   => "inlinehint"
    case Attr.Final        => "final"
  }

  implicit val showBlock: Show[Block] = Show { block =>
    import block._
    val header = sh"block $name(${r(params, sep = ", ")})"
    val body = i(r(instrs, sep = nl("")))
    sh"$header =$body"
  }

  implicit val showInstr: Show[Instr] = Show {
    case Instr(None, attrs, op)       => sh"$attrs$op"
    case Instr(Some(name), attrs, op) => sh"$name = $attrs$op"
  }

  implicit val showParam: Show[Param] = Show {
    case Param(name, ty) => sh"$name: $ty"
  }

  implicit val showNext: Show[Next] = Show {
    case Next(name, args) =>
      sh"$name(${r(args, sep = ", ")})"
  }

  implicit val showCase: Show[Case] = Show {
    case Case(value, next) =>
      sh"case $value => $next"
  }

  implicit val showOp: Show[Op] = Show {
    case Op.Undefined =>
      "undefined"
    case Op.Ret(value) =>
      sh"ret $value"
    case Op.Throw(value) =>
      sh"throw $value"
    case Op.Jump(next) =>
      sh"jump $next"
    case Op.If(cond, thenp, elsep) =>
      sh"""if $cond
      then $thenp
      else $elsep"""
    case Op.Switch(scrut, default, cases)  =>
      sh"""switch $scrut
      ${r(cases, sep = nl("  "))}
      case _ => $default"""
    case Op.Invoke(f, args, succ, fail) =>
      sh"""invoke $f(${r(args, sep = ", ")})
      to $succ
      unwind $fail"""

    case Op.Call(ty, f, args) =>
      sh"call[$ty] $f(${r(args, sep = ", ")})"
    case Op.Load(ty, ptr) =>
      sh"load[$ty] $ptr"
    case Op.Store(ty, ptr, value) =>
      sh"store[$ty] $ptr, $value"
    case Op.Elem(ty, ptr, indexes) =>
      sh"element[$ty] $ptr, ${r(indexes, sep = ", ")}"
    case Op.Extract(ty, aggr, index) =>
      sh"extract[$ty] $aggr, $index"
    case Op.Insert(ty, aggr, value, index) =>
      sh"insert[$ty] $aggr, $index"
    case Op.Alloc(ty) =>
      sh"alloc[$ty]"
    case Op.Alloca(ty) =>
      sh"alloca[$ty]"
    case Op.Size(ty) =>
      sh"size[$ty]"
    case Op.Bin(name, ty, l, r) =>
      sh"$name[$ty] $l, $r"
    case Op.Comp(name, ty, l, r) =>
      sh"$name[$ty] $l, $r"
    case Op.Conv(name, ty, v) =>
      sh"$name[$ty] $v"

    case Op.FieldElem(ty, name, value) =>
      sh"field-elem[$ty] $name, $value"
    case Op.MethodElem(ty, name, value) =>
      sh"method-elem[$ty] $name, $value"
    case Op.AllocClass(ty) =>
      sh"alloc-class[$ty]"
    case Op.AllocArray(ty, length) =>
      sh"alloc-array[$ty] $length"
    case Op.Equals(left, right) =>
      sh"equals $left, $right"
    case Op.HashCode(value) =>
      sh"hash-code $value"
    case Op.GetClass(value) =>
      sh"get-class $value"
    case Op.AsInstanceOf(value, ty) =>
      sh"as-instance-of[$ty] $value"
    case Op.IsInstanceOf(value, ty) =>
      sh"is-instance-of[$ty] $value"
    case Op.ArrayLength(value) =>
      sh"array-length $value"
    case Op.ArrayElem(ty, value, index) =>
      sh"array-elem[$ty] $value, $index"
    case Op.Box(ty, value) =>
      sh"box[$ty] $value"
    case Op.Unbox(ty, value) =>
      sh"unbox[$ty] $value"
    case Op.MonitorEnter(v) =>
      sh"monitor-enter $v"
    case Op.MonitorExit(v) =>
      sh"monitor-exit $v"
    case Op.StringConcat(l, r) =>
      sh"string-concat $l, $r"
    case Op.ToString(v, Val.None) =>
      sh"to-string $v"
    case Op.ToString(v, radix) =>
      sh"to-string $v, $radix"
    case Op.FromString(ty, s, Val.None) =>
      sh"from-string[$ty] $s"
    case Op.FromString(ty, s, radix) =>
      sh"from-string[$ty] $s, $radix"
  }

  implicit val showBin: Show[Bin] = Show {
    case Bin.Add  => "add"
    case Bin.Sub  => "sub"
    case Bin.Mul  => "mul"
    case Bin.Div  => "div"
    case Bin.Mod  => "mod"
    case Bin.Shl  => "shl"
    case Bin.Lshr => "lshr"
    case Bin.Ashr => "ashr"
    case Bin.And  => "and"
    case Bin.Or   => "or"
    case Bin.Xor  => "xor"
  }

  implicit val showComp: Show[Comp] = Show {
    case Comp.Eq  => "eq"
    case Comp.Neq => "neq"
    case Comp.Lt  => "lt"
    case Comp.Lte => "lte"
    case Comp.Gt  => "gt"
    case Comp.Gte => "gte"
  }

  implicit val showConv: Show[Conv] = Show {
    case Conv.Trunc    => "trunc"
    case Conv.Zext     => "zext"
    case Conv.Sext     => "sext"
    case Conv.Fptrunc  => "fptrunc"
    case Conv.Fpext    => "fpext"
    case Conv.Fptoui   => "fptoui"
    case Conv.Fptosi   => "fptosi"
    case Conv.Uitofp   => "uitofp"
    case Conv.Sitofp   => "sitofp"
    case Conv.Ptrtoint => "ptrtoint"
    case Conv.Inttoptr => "inttoptr"
    case Conv.Bitcast  => "bitcast"
  }

  implicit val showVal: Show[Val] = Show {
    case Val.None               => ""
    case Val.True               => "true"
    case Val.False              => "false"
    case Val.Zero(ty)           => sh"zero[$ty]"
    case Val.I8(value)          => sh"${value}i8"
    case Val.I16(value)         => sh"${value}i16"
    case Val.I32(value)         => sh"${value}i32"
    case Val.I64(value)         => sh"${value}i64"
    case Val.F32(value)         => sh"${value}f32"
    case Val.F64(value)         => sh"${value}f64"
    case Val.Struct(ty, values) => sh"struct[$ty] ${r(values, ", ")}"
    case Val.Array(ty, values)  => sh"array[$ty] ${r(values, ", ")}"
    case Val.Local(name, ty)    => sh"$name"
    case Val.Global(name, ty)   => sh"$name"

    case Val.Unit      => "unit"
    case Val.Null      => "null"
    case Val.String(v) => "\"" + v.replace("\"", "\\\"") + "\""
    case Val.Class(ty) => sh"class[$ty]"
  }

  implicit val showDefns: Show[Seq[Defn]] = Show { defns =>
    r(defns, sep = nl(""))
  }

  implicit val showDefn: Show[Defn] = Show {
    case Defn.Var(attrs, name, ty, rhs) =>
      sh"${attrs}var $name: $ty = $rhs"
    case Defn.Declare(attrs, name, ty) =>
      sh"${attrs}def $name: $ty"
    case Defn.Define(attrs, name, ty, blocks) =>
      val body = r(blocks.map(i(_)))
      sh"${attrs}def $name: $ty =$body"
    case Defn.Struct(attrs, name, members) =>
      sh"${attrs}struct $name {${r(members, sep = ", ")}}"

    case Defn.Interface(attrs, name, ifaces, members) =>
      val parents = r(ifaces, sep = ", ")
      val body = r(members.map(i(_)))
      sh"${attrs}interface $name($parents) =$body"
    case Defn.Class(attrs, name, parent, ifaces, members) =>
      val parents = r(parent +: ifaces, sep = ", ")
      val body = r(members.map(i(_)))
      sh"${attrs}class $name($parents) =$body"
    case Defn.Module(attrs, name, parent, ifaces, members) =>
      val parents = r(parent +: ifaces, sep = ", ")
      val body = r(members.map(i(_)))
      sh"${attrs}module $name($parents) =$body"
  }

  implicit val showType: Show[Type] = Show {
    case Type.None                => ""
    case Type.Void                => "void"
    case Type.Size                => "size"
    case Type.Bool                => "bool"
    case Type.I8                  => "i8"
    case Type.I16                 => "i16"
    case Type.I32                 => "i32"
    case Type.I64                 => "i64"
    case Type.F32                 => "f32"
    case Type.F64                 => "f64"
    case Type.Array(ty, n)        => sh"[$ty x $n]"
    case Type.Ptr(ty)             => sh"ptr ${ty}"
    case Type.Function(args, ret) => sh"(${r(args, sep = ", ")}) => $ret"
    case Type.Struct(name)        => name

    case Type.Unit           => "unit"
    case Type.Nothing        => "nothing"
    case Type.NullClass      => "null"
    case Type.ObjectClass    => "object"
    case Type.ClassClass     => "class"
    case Type.StringClass    => "string"
    case Type.CharacterClass => "char"
    case Type.BooleanClass   => "boolean"
    case Type.ByteClass      => "byte"
    case Type.ShortClass     => "short"
    case Type.IntegerClass   => "integer"
    case Type.LongClass      => "long"
    case Type.FloatClass     => "float"
    case Type.DoubleClass    => "double"
    case Type.Class(name)    => sh"class $name"
    case Type.ArrayClass(ty) => sh"${ty}[]"
  }

  implicit val showGlobal: Show[Global] = Show {
    case Global.Atom(id)              => id
    case Global.Nested(owner, member) => sh"$owner::$member"
    case Global.Tagged(n, tag)        => sh"${n}_$tag"
  }

  implicit val showLocal: Show[Local] = Show {
    case Local(id) => sh"%$id"
  }
}