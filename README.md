


PROYECTO DE AULA

Z-ONE: SISTEMA DE GESTIÓN PARA PRODUCTORA MUSICAL


Jesús Figueroa
Álvaro Cadavid
Andrea Flórez

Ing. Esp. Alfredo Bautista

PROGRAMACIÓN DE COMPUTADORES III

FACULTAD DE INGENIERÍA Y TECNOLOGÍA
UNIVERSIDAD POPULAR DEL CESAR
VALLEDUPAR / CESAR

MAYO, 2026


Tabla de Contenido
Resumen…………………………………………………………………………………..
Introducción……………………………………………………………………………….
Planteamiento del Problema…………………………………………………………….
Justificación……………………………………………………………………………….
Estado del Arte / Vigencia Tecnológica………………………………………………..
Objetivos…………………………………………………………………………………..
Objetivo General………………………………………………………………………….
Objetivos Específicos…………………………………………………………………….
Descripción de la Solución………………………………………………………………
Descripción General……………………………………………………………………...
Requerimientos Funcionales…………………………………………………………….
Módulo 1: Gestión de Artistas…………………………………………………………...
Módulo 2: Gestión de Productor y Equipo Técnico…………………………………...
Módulo 3: Gestión de Catálogo Musical……………………………………………….
Módulo 4: Proceso de Producción……………………………………………………...
Módulo 5: Agenda y Planificación………………………………………………………
Módulo 6: Motor de Búsqueda y Filtrado………………………………………………
Módulo 7: Estadísticas de la Productora………………………………………………
Módulo 8: Interfaz Gráfica en Java Swing……………………………………………..
Módulo 9: Chatbot Temático…………………………………………………………….
Requerimientos No Funcionales………………………………………………………..
Gestión del Proyecto en GitHub………………………………………………………...
Repositorio en GitHub……………………………………………………………………
Definición de Roles del Equipo………………………………………………………….
Estrategia de Ramas……………………………………………………………………..
Tabla de Actividades Asignadas por Integrante………………………………………
Conclusiones……………………………………………………………………………...
FASE 2…………………………………………………………………………………….
Arquitectura del Proyecto………………………………………………………………..
Diagrama de Paquetes Organizados por Capas……………………………………...
Identificación de Clases………………………………………………………………….
Definición de Atributos, Métodos y Responsabilidades………………………………
Relaciones entre Clases…………………………………………………………………
Incorporación de Herencia, Interfaces y Clases Abstractas…………………………
Aplicación de Principios de Diseño Orientado a Objetos…………………………….
Bibliografía………………………………………………………………………………...




Resumen
Este sistema busca digitalizar y centralizar los procesos operativos de una productora musical, abarcando la gestión de artistas, productores, equipo técnico, catálogo musical, proceso de producción, agenda, búsqueda y estadísticas. En esta fase se establece el problema identificado, la justificación del proyecto, el estado del arte, los objetivos, la descripción de la solución con sus requerimientos funcionales y no funcionales, y la estructura de gestión del proyecto mediante Git y GitHub.
El presente documento constituye las fases iniciales del proyecto de aula.

Introducción
La industria musical opera en un entorno de alta complejidad administrativa y logística. En este contexto, las productoras musicales desempeñan un papel fundamental al coordinar procesos creativos, técnicos y administrativos que garantizan la producción y distribución eficiente del contenido musical de calidad, todo de manera simultánea y bajo fechas estipuladas.
La gestión eficiente de una productora musical requiere control detallado sobre múltiples componentes, tales como la administración de artistas, productores, proyectos y recursos. Cuando estos procesos se realizan de manera manual o mediante herramientas básicas o dispersas, surgen dificultades para mantener la trazabilidad de la información, coordinar recursos, brindar un buen servicio, así como desorden de datos y limitada capacidad para tomar decisiones oportunas basadas en información confiable.
Por ello, la implementación de un sistema especializado se convierte en una necesidad estratégica. Un sistema de este tipo permite centralizar la información, estructurar los procesos de forma lógica, mejorar la organización interna y optimizar el uso de los recursos disponibles, facilitando así la administración integral de la productora.
En este sentido, Z-one se propone como una solución tecnológica necesaria para este sector, orientada a centralizar la información, optimizar la gestión de recursos y simular de manera estructurada el funcionamiento real de una productora musical según sus necesidades. Además, incorpora un chatbot temático que mejora la interacción con el usuario, brindando una experiencia más dinámica, organizada y accesible. De esta manera, la plataforma contribuye a fortalecer la eficiencia administrativa y la calidad del servicio ofrecido.





Planteamiento del Problema
Actualmente, la industria musical ha pasado a ser ejecutada en gran parte por productoras independientes, las cuales enfrentan desafíos en la gestión de sus operaciones internas. El manejo de información relacionada con artistas, productores, equipo técnico, recursos y planificación de trabajo se realiza cotidianamente mediante registros dispersos o procesos manuales, lo que dificulta el control eficiente de las operaciones y limita la capacidad de respuesta frente a cambios o necesidades del entorno.
La carencia de una plataforma que centralice toda la información genera problemas como inconsistencia de datos, falta de claridad en los proyectos, conflictos en la programación y asignación de recursos, escasa trazabilidad y limitada capacidad de análisis estadístico para la toma de decisiones estratégicas. Esta situación crea una brecha tecnológica entre lo que el estudio necesita para operar eficientemente y las herramientas con las que realmente cuenta.
Esta situación evidencia la necesidad de desarrollar una solución que pueda ser implementada y que resuelva el problema de administración desorganizada en todos los procesos asociados a una producción musical.

Justificación
Una productora musical requiere un sistema que centralice la información de sus operaciones de gestión: manejo de artistas, productores, equipo técnico y recursos, y que garantice la trazabilidad de cada proyecto musical a través de sus fases de producción. Es un escenario real y complejo; mediante la implementación de un sistema de gestión basado en Programación Orientada a Objetos será posible entender el negocio, sistematizar y automatizar sus operaciones, garantizando eficiencia, consistencia y escalabilidad.

Estado del Arte / Vigencia Tecnológica
En el mercado existen plataformas orientadas a la gestión de la industria musical que ofrecen administración de catálogo, sistemas de planificación de estudios de grabación y herramientas de análisis de datos. Sin embargo, estas soluciones están orientadas principalmente a funciones específicas y no integran todas las funcionalidades en un mismo programa.
Z-one se diferencia al proporcionar un sistema de información integral: es un sistema integrado y escalable que modela el ciclo de vida completo de una producción musical desde la perspectiva administrativa y operativa interna de la empresa, desarrollado en Java.

Objetivos
Objetivo General
Desarrollar un sistema de gestión para una productora musical que permita administrar de forma eficiente artistas, productores, catálogo musical, procesos de producción, planificación y estadísticas operativas mediante el lenguaje Java.

Objetivos Específicos
•Diseñar una estructura orientada a objetos que modele las entidades y relaciones propias de una productora musical.
•Implementar módulos funcionales para la gestión de artistas, productores y catálogo musical.
•Desarrollar mecanismos de seguimiento para los procesos de producción y planificación de actividades.
•Integrar herramientas de búsqueda y filtrado que faciliten la consulta de información.
•Implementar funcionalidades estadísticas que permitan analizar el rendimiento operativo y musical de la productora.
•Desarrollar una interfaz gráfica profesional y adaptable en Java Swing.
•Incorporar un asistente conversacional que responda dudas frecuentes sobre música y el uso de la plataforma.
•Utilizar Git y GitHub para controlar versiones y definir una estrategia de trabajo colaborativo.
•Realizar la respectiva documentación del sistema.












Descripción de la Solución
Descripción General
La solución propuesta consiste en desarrollar un sistema de información de escritorio en Java, basado en una arquitectura modular orientada a objetos. Z-one centraliza la administración operativa de una productora musical a través de módulos funcionales integrados, cada uno con responsabilidades claramente delimitadas y comunicados entre sí para garantizar la coherencia del flujo de trabajo empresarial.
Permitirá registrar artistas según sus características, controlar sus estados de vinculación, gestionar colaboraciones, historial y actualizar la composición de bandas. También administrará productores y equipos técnicos, permitiendo asignaciones según disponibilidad y especialización.
El módulo de catálogo musical permitirá gestionar canciones, versiones, estados de producción y agrupación por proyectos musicales. Por su parte, el módulo de producción controlará las fases del proceso creativo, las sesiones de grabación y la disponibilidad de cabinas.
Adicionalmente, el sistema incorporará una agenda centralizada, filtros avanzados de búsqueda y un módulo estadístico que permitirá visualizar métricas relevantes para apoyar la toma de decisiones. También se cuenta con un chatbot asistencial para hacer más amigable la experiencia en el sistema.

Requerimientos Funcionales
Z-one implementa un conjunto completo de funcionalidades organizadas en módulos principales. Cada función responde directamente a una necesidad operativa real del estudio de producción musical y fue diseñada siguiendo los principios de programación orientada a objetos, arquitectura en capas y código limpio, con separación de responsabilidades y escalabilidad del sistema.

Módulo 1: Gestión de Artistas
Este módulo se encarga del núcleo administrativo del sistema. Un artista representa quien produce el contenido musical y la productora necesita registrar toda esta información, ya que los artistas son parte central del sistema de negocio.

•Funcionalidad 1: Registrar, clasificar, consultar, eliminar, actualizar y controlar el estado de un artista registrado en la productora. Los atributos son:
·Nombre artístico y nombre real
·Fecha de nacimiento, género y nacionalidad
·Géneros musicales principales
·Redes sociales y canales oficiales vinculados
·Foto de perfil y galería de imágenes promocionales
·Fecha de firma con la productora

•Funcionalidad 2: Distinguir entre los distintos tipos de artistas, cada uno con sus propias reglas según sus características:
·Solista: conformado por una persona.
·Banda: conformada por más de una persona.
·Invitado: participante que no pertenece como miembro de la productora; puede ser solista o banda.

•Funcionalidad 3: Gestionar los estados posibles en los que se puede encontrar un artista dentro del sistema:
·En Negociación: artista siendo evaluado por la productora.
·Activo: miembro de la productora.
·En Pausa: firmado por la productora, pero sin actividades temporalmente.
·Retirado: terminó su vinculación con la productora.

•Funcionalidad 4: Registrar colaboraciones entre artistas cuando estas se realicen. Los artistas de la productora pueden colaborar con otros artistas propios o con invitados.
•Funcionalidad 5: Gestionar actualizaciones en las bandas, dado que los integrantes pueden entrar y salir, y se guardará el historial de cambios.

Módulo 2: Gestión de Productor y Equipo Técnico
El productor y su equipo técnico son quienes coordinan, definen y transforman una producción musical. Este módulo administra toda la información relacionada con este personal.

•Funcionalidad 1: Registrar, clasificar, consultar, eliminar, actualizar y controlar el estado de un productor junto a su equipo técnico. Los atributos son:
·Nombre del productor
·Nombre del integrante del equipo técnico y su respectivo cargo
·Fecha de nacimiento, género y nacionalidad
·Géneros musicales especializados
·Años de experiencia
·Foto de perfil
·Fecha de firma con la productora

•Funcionalidad 2: Gestionar los estados posibles en los que se puede encontrar un productor o miembro del equipo técnico:
·Disponible: libre para ser asignado a un proyecto.
·Asignado: trabajando activamente en proyectos.
·Sobrecargado: no disponible para ser asignado a otro proyecto.
·No Disponible: sin actividades temporalmente.
·Inactivo: terminó su vinculación con la productora.

Módulo 3: Gestión de Catálogo Musical
El catálogo musical es el producto final que ofrece una productora. Cada elemento debe estar documentado con precisión, ya que son clave para el beneficio de la empresa.

•Funcionalidad 1: Registrar, clasificar, consultar, eliminar, actualizar y organizar el contenido musical producido en la productora. Los atributos de una canción son:
·Título oficial y títulos alternativos, si existen.
·Duración exacta almacenada en segundos internamente para precisión en cálculos.
·Género principal y subgénero que ubican la canción en el ecosistema musical.
·BPM (beats per minute) que define el tempo y es clave para listas de reproducción.
·Créditos detallados: nombre del artista y del productor junto a su equipo técnico.
·Fecha de composición y fecha de publicación.
·Idioma principal de la letra.

•Funcionalidad 2: Gestionar los estados posibles de una canción a lo largo de su ciclo de vida. El estado determina qué operaciones están permitidas sobre ella:
·En Producción: siendo trabajada activamente en el Módulo 4.
·Lista: master entregado y aprobado.
·Publicada: disponible para el acceso del público.
·Archivada: guardada en el sistema, nunca salió al mercado.
·Retirada: retirada del mercado activo, archivada en el sistema.

•Funcionalidad 3: Gestionar múltiples versiones de una misma canción original. Las versiones posibles son:
·Versión Original: grabación principal.
·Remix: reinterpretación productiva por un tercero.
·Versión Acústica: arreglo despojado de producción electrónica.
·Demo: versión preliminar de uso interno, no distribuible al público.

•Funcionalidad 4: Agrupar canciones en proyectos musicales según su identidad e intención. Los tipos de proyectos son:
·Sencillo (Single): una canción única, ya sea adelanto, producto único o independiente.
·EP (Extended Play): proyecto corto de tres a cinco canciones.
·Álbum de Estudio: obra completa del artista; abarca de seis a quince canciones.
·Compilación: recopilación de canciones temáticas o éxitos.
·Demo: versión preliminar de uso interno, no distribuible al público.

Módulo 4: Proceso de Producción
La producción musical es el proceso central del cual se encarga una productora. Es un proceso complejo y secuencial que involucra diferentes recursos y tiempos estipulados. Sin un proceso eficiente se pierde la trazabilidad del desarrollo de un proyecto musical.

•Funcionalidad 1: Gestionar las fases del proceso productivo. La transición de una fase a la siguiente solo ocurre cuando la anterior ha sido completada:
·En Composición: idea o borrador; aún no se ha iniciado el proceso de producción.
·En Grabación: se están realizando sesiones de grabación en el estudio.
·Arreglo: ajustes técnicos del producto; abarca mezcla y masterización.
·Entrega de Master: cierre formal del proceso productivo.
Al finalizar en la entrega de master, el módulo de producción notifica al catálogo para que cambie el estado de la canción de "En Producción" a "Lista".

•Funcionalidad 2: Gestionar la disponibilidad de cabinas para programar sesiones sin conflicto:
·Cabinas individuales con su equipamiento específico y condiciones técnicas.
·Estados de la cabina: Disponible, Ocupada, En Mantenimiento.

•Funcionalidad 3: Registrar y gestionar sesiones de grabación. Cada sesión es un evento operativo con los siguientes datos:
·Fecha, hora de inicio y hora de fin de la sesión de grabación.
·Estudio y cabina específica asignada (se contempla el caso de grabaciones en estudios externos en colaboraciones).
·Número, nombre y nota de la sesión de grabación.
·Estado de la sesión: Programada, Confirmada, En Curso, Finalizada, Cancelada.

Módulo 5: Agenda y Planificación
En una productora musical los recursos pueden ser limitados, ya sea por el recurso humano o por la capacidad del estudio. La planificación centralizada es clave para ofrecer un servicio eficiente y efectivo.

•Funcionalidad única: Visualizar el calendario de todas las fechas relevantes para:
·Artistas.
·Productores y su equipo técnico.
·Sesiones de grabación.
·Fechas de lanzamiento del catálogo musical según el tipo de proyecto (Sencillo, EP, Álbum de Estudio, Compilación).

Módulo 6: Motor de Búsqueda y Filtrado
Permitir la consulta de información de múltiples formas para mayor practicidad:
·Por artista, género musical, año o productor.
·Por estado: En Composición, En Grabación, Arreglo, Lista, Publicada, Archivada, Retirada.

Módulo 7: Estadísticas de la Productora
Los datos de una productora, bien procesados y analizados, revelan puntos clave para la toma de decisiones estratégicas. Este módulo permite conocer las fortalezas y debilidades del negocio.

•Por artista: Visualizar el rendimiento de un artista de acuerdo con reproducciones totales del catálogo musical por semana, mes y año.
•Por productor: Visualizar el rendimiento de un productor junto a su equipo técnico de acuerdo con reproducciones totales del catálogo en el que haya trabajado, y la cantidad de proyectos a los que fue asignado.

Tendencias y Ranking Internos:
·Top canciones de la semana y del mes dentro del catálogo de la productora.
·Comparativa de géneros musicales de la productora mensualmente.

Módulo 8: Interfaz Gráfica en Java Swing
Este módulo ofrece una forma visual e interactiva que complementa la aplicación de consola. Está desarrollado con Java Swing sin dependencias externas, lo que facilita su portabilidad y despliegue.

Módulo 9: Chatbot Temático
El proyecto incorpora un asistente conversacional que acompaña al usuario y resuelve de forma rápida las dudas más comunes que pueden surgirle mientras utiliza el sistema. Estas preguntas suelen estar relacionadas con géneros musicales, artistas, instrumentos, conceptos de teoría musical o con el funcionamiento de la propia plataforma.
El asistente procesa consultas escritas en lenguaje natural: lee el mensaje del usuario, identifica a qué categoría pertenece la pregunta y, a partir de su base de conocimientos, entrega la respuesta más adecuada. De esta manera, el chatbot funciona como un primer punto de ayuda dentro del sistema, ofreciendo información útil de manera inmediata y mejorando la experiencia general del usuario.

Requerimientos No Funcionales
•Usabilidad: La interfaz gráfica debe ser intuitiva y consistente, con formularios claros, mensajes de validación comprensibles y navegación fluida entre módulos.
•Rendimiento: Las operaciones de consulta y filtrado sobre el catálogo musical deben responder en un tiempo máximo de 3 segundos bajo condiciones normales de uso.
•Mantenibilidad: El código debe seguir las convenciones de nomenclatura Java, aplicar principios SOLID y estar documentado con Javadoc en todas las clases públicas.
•Escalabilidad: La arquitectura de capas y el patrón DAO deben permitir cambiar el motor de base de datos sin afectar la lógica de negocio ni la capa de presentación.
•Confiabilidad: El sistema debe manejar todas las excepciones de acceso a datos de forma controlada, evitando cierres inesperados y mostrando mensajes de error descriptivos al usuario.
•Seguridad básica: Los datos almacenados en la base de datos deben ser consistentes e íntegros, garantizados mediante restricciones de clave foránea y validación en la capa de negocio.
•Portabilidad: El sistema debe funcionar en cualquier equipo con JDK 11 o superior instalado y conectividad al motor de base de datos Oracle.
 commit del proyecto
 
 $ git log --oneline --all --graph --decorate
* 6571b40 (HEAD -> master, origin/master, origin/HEAD) feat:mejora en sessiones,grabaciones y chatbot
| * bb1617f (respaldo-final) eliminar audios del respositorio
| * f1654a2 (respaldo-seguriddad) feat:lo cambio montado
| * 7d2a5e1 chore:se removiero alguno archivo por el espacio del respositorio
| * ac8a2a1 feat:arreglo de una que otra arquitectura
| *   9e95c8a merge origin/Dev into master
| |\
| | * d8f2875 (origin/dev2) feat: mejora en aspecto de ingreso de datos en campos de fechas con placeholder
| | * 06d05b8 feat: cambio en formato de fecha de formproductor y mejora en ingreso de estado de productor con combobox
| | * 141c032 feat: arreglo en campo de fecha de agregar productor
| | * 7e407c5 feat: arreglo de diseño de agregar productor y mejora en validacion de correo
| | * b308e68 feat: refactorizacion de nombre del chat bot
| | * 6bb995b feat: refactorizacion de nombre del chat bot
| | * 4b4f295 fix: resolver conflictos de merge en vistas
| | *   14568cb fix: eliminar clases de factura no existentes en master
| | |\
| |_|/
|/| |
| | *   3429687 feat: incorporacion de cambios del merge
| | |\
| | * | 5f97f4c feat: cambios de funcionamiento sin base de datos
| | * |   6516a84 Merge branch 'main' of https://github.com/jmfigueroa366/z_one into dev2
| | |\ \
| | | * | c01e6b4 feat:arregle la interface
| | * | | 4ea538b feat: aplicacion completa de cambios en rama main
| | |\| |
| | * | | 108493c feat: inicio de aplicacion de cambios de la rama main
| | * | | af76be9 feat: refactorización de clase formCatalogo para adaptar a modelo relacional con incorporacion de agregacion de generos
| | * | | de3010b feat: creacion y diseño de clase formCatalogo
| * | | | 0b98fab (respaldo-local) feat:actualizacion de respaldo
| * | | | 69764b3 feat:respaldo ante de sicronizar
|/ / / /
* | | | d739312 (respaldo-limpieza)  feat:arreglo de la interface y de la base de dato
* | | | 1f16020 feat:arreglo del diseño de factura
* | | | a40a17d feat:ultimo detalles!
| | | | *   8a49356 (main) resolver conflicto merge form cabina
| | | | |\
| |_|_|_|/
|/| | | |
* | | | | 104ed19 arregle los diseños de los dialogos
| | | | * 7641be1 (origin/main) actulizacion del proyecto
| |_|_|/
|/| | |
* | | | ae41e12 feat:conectar la base de dato con la de mi compañera me daria un error pero vamos arreglar
* | | | edc0d6a feat:60 % del programa terminadad,quiero agregar un tabla de estadista
* | | | ddc76ae feat:vista detalladas pero me falta configurar
* | | | f9218cf diseño sesion arreglado y mejorado
* | | | ef954af feat:arregle el diseño de sesion pero tengo un error al momento de ingresar una session
* | | | f764226 feat:arreglo del diseño view de form cancion
* | | | 2376402  feat:arreglando la capa view tenia un error por la clave apy me todo cambiar esa linea
* | | | 59bf7b7 feat:sigo arreglando lo diseño me toco cambiar la claves de apy por que no me deja hacer cambio ni ami compañero
* | | | 5564fe0 estoy arreglando lo diseño pero tube problema
| |_|/
|/| |
* | | aee46d7 feat:arquitectura,calendario y configuracion
* | | 07ac119 feat:me toco limpiar el historial de apy
| |/
|/|
* | 4447b84 feat:arreglo de las interfaces no estan terminadas aun
* | f3b6830 feat:logre conectar la base de dato y areglar los errores
* | e8b96dc Merge remote-tracking branch 'origin/dev2'
|\|
| * b226f5b creacion y diseño de clase formSesion
| *   6ac20b7 Merge branch 'main' of https://github.com/jmfigueroa366/z_one into dev2
| |\
| * \   ce23876 Merge branch 'main' of https://github.com/jmfigueroa366/z_one into dev2
| |\ \
| * | | 807ed6b feat: deiseño de clase formSesion
| * | | 2c25570 feat: creacion de clase formSesion
| * | | 70d04f4 feat: creacion de clase formSesion
* | | | cbfe83e feat:erro en lo diseño
| |_|/
|/| |
* | | 030ab5a feat:agregado la base de a service y agregar el diseño del panel de productores
| |/
|/|
* | 6446979 feat:corregi el contrusctor de artista y rediseñe el formular de artista
* | 440be86 merge:integrar dev2 - modulo catalogo
|\|
| * 45efa40 feat: correcion de nombres variables de botones de clase formProductores (faltando backend)
| * 8592b38 feat: diseño base de clase formProductores (faltando backend)
| * d653e51 creacion de clase formProductores
* | 85a046b dev1:problema resuelto
* | 83f3fa9 resolviendo lo errores
* | 2c566f3 merge:integrar dev2 - modulo catalogo
|\|
| * a7bf22a creacion de clase de formArtistas con diseño boceto
* | 36a87b3 feat:animacion transicion login_registro y mejora ui
|/
* d2b6e89 feat:MascoPanel,modeNUI,dashboard,pane y ajuste generales
*   32896f3 merge:integrar dev2,conservar credenciales locales de conexiondb
|\
| * dc2c31c feat: creacion de clase interface IDao<T> con sus metodos
| * ab61e14 feat: diseño de clase Album;
| * 693eb93 diseño de clase cancion
| * 5c16d04 Agregacion de metodo calcularCosto en clase sesion
| * 007d700 feat: diseño de clase sesion
| * eb5e779 feat: diseño de clase productor
| * 362cdbc feat: diseño de clase persona
| *   d299cc3 feat: commit prueba de cambios
| |\
| * | f2b4bde feat: creacion de clase abstracta persona
* | | 1112ceb FEAT:agregar mascotapanel animado al login
| |/
|/|
* | 6d18b25 feact:agregar clases utili,view y lidbreria de sql
* | 4e11288 feat: agregar estructura del proyecto musical
|/
* b476f08 (origin/dev3, origin/dev1, dev3, dev2, dev1) feat:agregar estructura de clase del proyecto
* 00976eb corregir gitignore para netbeans
* 46dbde5 primer commit: proyecto inicial

Gestión del Proyecto en GitHub
Repositorio en GitHub
Repositorio: https://github.com/jmfigueroa366/z_one

Definición de Roles del Equipo
Cada miembro del equipo tuvo un rol específico en el proyecto. La asignación de roles busca distribuir la carga de trabajo de manera equitativa y garantizar que cada persona sepa exactamente qué debe hacer en su área del proyecto.

Rol 1: Full-Stack — Líder del Proyecto
Integrante	Jesús Figueroa
Rol	Líder del proyecto / Full-Stack
Módulo asignado	Módulo 1: Artistas y Productores
Rama de trabajo	feature/modulo1-artistas

El líder tiene varias tareas importantes: crear las clases para el artista y el productor junto con sus respectivos DAO y Servicios, y definir la estructura básica del proyecto (paquetes, clase principal y arquitectura general del sistema).

Responsabilidad Git	Descripción
Administrar el repositorio	Configura el repositorio en GitHub, define la estructura inicial y establece las reglas de protección de la rama main.
Revisar Pull Requests	Revisa y aprueba las solicitudes de fusión de los otros integrantes antes de integrarlas a la rama dev.
Gestionar fusiones a main	Es el único integrante autorizado para fusionar cambios de dev hacia main una vez verificada la estabilidad del sistema.
Resolver conflictos críticos	Cuando dos ramas generan conflictos que no pueden resolverse de forma individual, el líder interviene para resolverlos.
Mantener el README	Actualiza el archivo README.md con la información general del proyecto, instrucciones de instalación y descripción de funcionalidades.

Rol 2: Backend — Desarrolladora de la base de datos
Integrante	Andrea Florez
Rol	Desarrolladorade la base de datos/ Backend
Módulo asignado	Base de datos en sql developer
Rama de trabajo	feature/sql

Este integrante se enfoca en la parte central del sistema, donde se aplican las reglas del negocio del estudio. Su trabajo es desarrollar la base de datos que se conectará con el programa para llevar a cabo la persistencia de archivos en la capa DAO:
•Generar las tablas relacionales en sql developer.
•Relacionar entidades (objetos) de forma que queden con un modelo adecuado para el desarrollo.
•Asegurar que cada sesión se vincule correctamente con su artista y su productor.

Responsabilidad Git	Descripción
Trabajar en rama propia	Desarrolla todo el módulo 2 en la rama feature/modulo2-sesiones, sin modificar directamente dev ni main.
Realizar commits descriptivos	Cada cambio se sube con un mensaje claro (por ejemplo: "Agrega cálculo automático de costo de sesión").
Abrir Pull Requests	Cuando termina una tarea, abre un PR hacia dev y solicita la revisión del líder.
Atender observaciones	Aplica los cambios sugeridos durante la revisión del PR antes de la fusión.
Sincronizar con dev	Antes de subir nuevos cambios, hace pull de dev para evitar conflictos.

Rol 3: Frontend — Desarrollador de Interfaz Gráfica
Integrante	Jesús Figueroa
Rol	Desarrolladora de interfaz gráfica / Frontend
Módulo asignado	Módulo 8: Diseño Java Profesional y Responsive
Rama de trabajo	feature/modulo3-interfaz

Este miembro del equipo es el encargado de la parte visual de la aplicación. Su tarea es crear las ventanas y formularios en Java Swing, mantener el sistema de constantes de diseño (DesignSystem.java) y asegurar que la interfaz se adapte a diferentes tamaños de pantalla utilizando los puntos de interrupción definidos. También conecta los formularios con los servicios desarrollados por los demás integrantes.

Responsabilidad Git	Descripción
Trabajar en rama propia	Desarrolla todos los cambios de interfaz en feature/modulo3-interfaz.
Mantener la rama actualizada	Hace pull frecuente de dev para integrar los cambios de los demás módulos.
Abrir Pull Requests	Solicita la fusión a dev cuando una pantalla o funcionalidad visual está terminada.
Realizar commits atómicos	Cada commit corresponde a un cambio visual concreto (por ejemplo: "Agrega formulario de registro de sesión").
Probar la integración visual	Verifica que las ventanas se vean correctamente después de cada merge.

Rol 4: Full-Stack / QA — Model y Documentación
Integrante	Álvaro Cadavid
Rol	Full-Stack / QA — Documentador y encargado de pruebas
Módulo asignado	Módulo 9: modelado de clases base y Documentación
Rama de trabajo	feature/model

Este integrante tiene dos responsabilidades principales: desarrollar y mantener paquete model y documentar todo el proyecto, verificando que cada parte funcione correctamente. Redacta el manual del usuario, mantiene la documentación técnica al día y reporta los errores detectados durante las pruebas para que los demás integrantes puedan corregirlos.

Responsabilidad Git	Descripción
Trabajar en rama propia	Desarrolla el chatbot en feature/modulo4-chatbot y la documentación en docs/.
Reportar incidencias	Crea issues en GitHub cuando detecta errores en otros módulos durante las pruebas.
Mantener la documentación	Sube los archivos .md con los manuales, guía de usuario y descripciones de funcionalidades.
Verificar la rama dev	Después de cada fusión a dev, ejecuta pruebas de integración para asegurar que el sistema sigue funcionando.
Apoyar al líder	Colabora con el líder en la revisión de Pull Requests cuando se requiere una segunda opinión.

Estrategia de Ramas
El proyecto utiliza una estrategia de ramas que permite trabajar en paralelo sin afectar la versión estable de la aplicación. Las ramas principales son:
•main: Rama protegida que contiene la versión estable del proyecto. Solo se actualiza cuando una versión está totalmente probada y lista para entregar. El líder del proyecto es el único que puede hacer fusiones hacia esta rama.
•dev: Rama de integración donde se combinan los avances de los distintos integrantes antes de pasar a main. Funciona como espacio de prueba para verificar que todos los módulos trabajen correctamente entre sí.
•feature/modulo1-artistas, feature/modulo2-sesiones, feature/modulo3-interfaz, feature/modulo4-chatbot: Ramas individuales, una por cada integrante. Cada uno desarrolla su módulo en su propia rama y abre un Pull Request hacia dev cuando termina una funcionalidad.
•fix/<nombre>: Ramas de corrección que se crean cuando se detecta un error específico que necesita resolverse rápidamente, sin esperar a un nuevo ciclo de desarrollo.
El flujo general de trabajo es: cada integrante trabaja en su propia rama → abre un Pull Request hacia dev → el líder revisa y aprueba → se fusiona a dev → cuando dev está estable, el líder fusiona a main.

Tabla de Actividades Asignadas por Integrante
Integrante	Rol	Módulo	Rama	Actividades Principales
Jesús Figueroa	Full-Stack	Módulo 1: Artistas y Productores	feature/modulo1-artistas	Configurar el repositorio, definir la arquitectura, crear las clases Artista y Productor con sus DAO y Servicios, revisar Pull Requests, fusionar a main y mantener el README.
Andrea florez	Backend	Módulo 2: Sesiones de Grabación	feature/modulo2-sesiones	Crear la clase Sesion con su DAO y Servicio, implementar el cálculo automático del costo, vincular sesiones con artistas y productores, abrir PRs hacia dev.
Jesús Figueroa	Frontend	Módulo 8: Diseño Java Profesional y Responsive	feature/modulo3-interfaz	Diseñar las ventanas en Java Swing, mantener DesignSystem.java, implementar los breakpoints responsive, conectar los formularios con los servicios del sistema.
Álvaro Cadavid	Full-Stack / QA	Módulo 9: Clases base y Documentación	feature/model-Modelado	Creacion de clases preliminares para el programa aplicando herencia, encapsulamiento y uso de clases abstractas e interface junto con la documentación del proyecto



Conclusiones
El análisis del dominio de una productora musical revela un entorno de alta complejidad operativa, donde la gestión de información desagregada y la ausencia de trazabilidad representan obstáculos reales para la eficiencia empresarial. La formulación del sistema Z-one responde a esta problemática con un diseño estructurado que modela con fidelidad los procesos internos del negocio, desde la vinculación de artistas y la producción de canciones hasta la publicación y el análisis de rendimiento.
La identificación de los requerimientos funcionales y no funcionales evidencia que el sistema requiere una arquitectura robusta, con jerarquías de herencia bien definidas, control riguroso de estados y transiciones, y una capa de persistencia que garantice la integridad de los datos.
La solución propuesta contribuirá a optimizar procesos operativos, mejorar la organización de recursos y centralizar la información del negocio. Además, fortalecerá competencias técnicas relacionadas con análisis, diseño y desarrollo de software modular en Java.

















FASE 2
Arquitectura del Proyecto
El sistema Z-one se construyó siguiendo una arquitectura orientada a objetos por capas, donde cada capa tiene una responsabilidad clara dentro del sistema. Esta separación garantiza que el código sea fácil de mantener, probar y ampliar, ya que cada parte cumple un único propósito y se comunica con las demás a través de interfaces bien definidas.
Tabla UML



Modelo de base de datos

Diagrama de Paquetes Organizados por Capas
co.zone.app
   └── Main.java                   ← Punto de entrada de la aplicación
co.zone.modelo                    ← Capa de Modelo
   ├── Persona.java    (Abstracta)
   ├── Artista.java    (Concreta)
   ├── Productor.java  (Concreta)
   ├── Sesion.java     (Concreta)
   ├── Cancion.java    (Concreta)
   └── Album.java      (Concreta)
co.zone.dao                       ← Capa de Persistencia
   ├── IDao.java       (Interfaz)
   ├── ArtistaDAO.java
   ├── ProductorDAO.java
   ├── SesionDAO.java
   ├── CancionDAO.java
   └── AlbumDAO.java
co.zone.servicio                  ← Capa de Lógica de Negocio
   ├── ArtistaServicio.java
   ├── ProductorServicio.java
   ├── SesionServicio.java
   ├── CancionServicio.java
   └── AlbumServicio.java
co.zone.vista                     ← Capa de Vista (Java Swing)
   ├── MainFrame.java
   ├── FormArtista.java
   ├── FormProductor.java
   ├── FormSesion.java
   ├── FormCancion.java
   ├── FormAlbum.java
   ├── PanelChatbot.java
   └── PanelEstadisticas.java
co.zone.util                      ← Capa de Utilidades
   ├── DesignSystem.java
   ├── Validador.java
   ├── ResponsiveManager.java
   └── Chatbot.java
Identificación de Clases
El sistema Z-one se compone de treinta clases principales distribuidas en sus respectivas capas. La siguiente tabla las identifica, indicando su tipo y rol en el sistema.

#	Nombre de la Clase	Tipo	Descripción y Rol en el Sistema
1	Main	Concreta	Punto de entrada del sistema. Inicializa la aplicación y muestra la ventana principal.
2	Persona	Abstracta	Clase padre que define los atributos comunes de toda persona del estudio. No se instancia directamente.
3	Artista	Concreta	Representa a un artista del estudio. Hereda de Persona y agrega el género musical.
4	Productor	Concreta	Representa a un productor del estudio. Hereda de Persona y agrega especialidad y tarifa por hora.
5	Sesion	Concreta	Representa una sesión de grabación. Vincula un artista con un productor y calcula automáticamente el costo total.
6	Cancion	Concreta	Representa una canción dentro del catálogo musical, asociada a un artista y a un álbum.
7	Album	Concreta	Representa un álbum musical compuesto por varias canciones de un mismo artista.
8	IDao<T>	Interfaz	Define el contrato CRUD genérico que deben implementar todos los DAO del sistema.
9	ArtistaDAO	Concreta	Implementa IDao<Artista>. Encargada de la persistencia de los artistas.
10	ProductorDAO	Concreta	Implementa IDao<Productor>. Encargada de la persistencia de los productores.
11	SesionDAO	Concreta	Implementa IDao<Sesion>. Encargada de la persistencia de las sesiones de grabación.
12	CancionDAO	Concreta	Implementa IDao<Cancion>. Encargada de la persistencia de las canciones del catálogo.
13	AlbumDAO	Concreta	Implementa IDao<Album>. Encargada de la persistencia de los álbumes.
14	ArtistaServicio	Concreta	Lógica de negocio asociada a los artistas (validaciones, búsquedas y operaciones).
15	ProductorServicio	Concreta	Lógica de negocio asociada a los productores.
16	SesionServicio	Concreta	Coordina las sesiones: valida datos, crea la sesión, calcula el costo y comunica vista con DAO.
17	CancionServicio	Concreta	Lógica de negocio de canciones: registro, asignación a álbumes y filtrado por género.
18	AlbumServicio	Concreta	Lógica de negocio de álbumes: registro, asociación de canciones y consulta del catálogo.
19	MainFrame	Concreta	Ventana principal de la aplicación. Contiene el menú lateral y el área de contenido.
20	FormArtista	Concreta	Formulario y listado de artistas. Permite registrar, editar y eliminar artistas.
21	FormProductor	Concreta	Formulario y listado de productores. Permite gestionar productores y su tarifa por hora.
22	FormSesion	Concreta	Formulario para programar sesiones de grabación. Muestra el costo total calculado automáticamente.
23	FormCancion	Concreta	Formulario para registrar canciones en el catálogo, asociándolas a un artista y un álbum.
24	FormAlbum	Concreta	Formulario para registrar álbumes y administrar las canciones que los componen.
25	PanelChatbot	Concreta	Panel visual del asistente conversacional. Se comunica con la clase Chatbot para procesar las consultas.
26	PanelEstadisticas	Concreta	Panel que muestra estadísticas y resultados de búsqueda/filtrado sobre los datos del estudio.
27	DesignSystem	Concreta	Clase de utilidad con constantes de diseño (colores, fuentes, espaciados). Centraliza la apariencia visual.
28	Validador	Concreta	Clase de utilidad con métodos estáticos para validar datos (cadenas, números, fechas, correos).
29	ResponsiveManager	Concreta	Aplica los breakpoints definidos y reorganiza los componentes de la interfaz al redimensionar la ventana.
30	Chatbot	Concreta	Implementa el asistente conversacional: carga la base de conocimientos en JSON, clasifica el mensaje y entrega la respuesta.

Definición de Atributos, Métodos y Responsabilidades

Clase 1: Main

Clase:	Main
Tipo:	Concreta
Responsabilidad principal:	Servir como punto de entrada del sistema, inicializando la aplicación y abriendo la ventana principal.

Métodos:
Método	Parámetros	Retorno	Descripción
main	String[] args	void	Arranca la aplicación e instancia MainFrame.

Clase 2: Persona

Clase:	Persona
Tipo:	Abstracta
Responsabilidad principal:	Modelar los atributos comunes de cualquier persona vinculada al estudio (artistas y productores), evitando duplicar código en las subclases.

Atributos:
Atributo	Tipo de Dato	Descripción
id	int	Identificador único de la persona.
nombre	String	Nombre completo.
correo	String	Correo electrónico.
telefono	String	Número telefónico.

Métodos:
Método	Parámetros	Retorno	Descripción
Persona	int, String, String, String	—	Constructor que inicializa los atributos comunes.
getId / setId	— / int	int / void	Acceso al identificador.
getNombre / setNombre	— / String	String / void	Acceso al nombre.
getCorreo / setCorreo	— / String	String / void	Acceso al correo.
getTelefono / setTelefono	— / String	String / void	Acceso al teléfono.
toString	—	String	Método abstracto, redefinido por cada subclase.

Clase 3: Artista

Clase:	Artista
Tipo:	Concreta
Responsabilidad principal:	Representar un artista del estudio, añadiendo a los datos básicos de Persona el género musical que interpreta.

Atributos:
Atributo	Tipo de Dato	Descripción
genero	String	Género musical principal del artista.

Métodos:
Método	Parámetros	Retorno	Descripción
Artista	int, String, String, String, String	—	Constructor que invoca al de Persona y asigna el género.
getGenero / setGenero	— / String	String / void	Acceso al género musical.
toString	—	String	Sobrescribe el método del padre.

Clase 4: Productor

Clase:	Productor
Tipo:	Concreta
Responsabilidad principal:	Representar un productor del estudio, añadiendo su especialidad y la tarifa por hora que cobra.

Atributos:
Atributo	Tipo de Dato	Descripción
especialidad	String	Especialidad (mezcla, masterización, grabación).
tarifaHora	double	Tarifa por hora de trabajo.

Métodos:
Método	Parámetros	Retorno	Descripción
Productor	int, String, String, String, String, double	—	Constructor que invoca al de Persona.
getEspecialidad / setEspecialidad	— / String	String / void	Acceso a la especialidad.
getTarifaHora / setTarifaHora	— / double	double / void	Acceso a la tarifa.
toString	—	String	Representación textual.

Clase 5: Sesion

Clase:	Sesion
Tipo:	Concreta
Responsabilidad principal:	Representar una sesión de grabación realizada en el estudio, vinculando un artista con un productor y calculando su costo total automáticamente.

Atributos:
Atributo	Tipo de Dato	Descripción
id	int	Identificador único.
fecha	LocalDate	Fecha de realización.
duracionHoras	double	Duración total en horas.
artista	Artista	Artista que participa.
productor	Productor	Productor a cargo.
costoTotal	double	Costo total calculado automáticamente.

Métodos:
Método	Parámetros	Retorno	Descripción
Sesion	int, LocalDate, double, Artista, Productor	—	Constructor que inicializa y calcula el costo.
calcularCosto	—	double	duracionHoras * productor.getTarifaHora().
getId, getFecha, getDuracionHoras, getArtista, getProductor, getCostoTotal	—	varios	Métodos de acceso.
setFecha, setDuracionHoras, setArtista, setProductor	varios	void	Modifican atributos y recalculan el costo.
toString	—	String	Representación textual.

Clase 6: Cancion

Clase:	Cancion
Tipo:	Concreta
Responsabilidad principal:	Representar una canción dentro del catálogo musical, asociada a un artista y, opcionalmente, a un álbum.

Atributos:
Atributo	Tipo de Dato	Descripción
id	int	Identificador único.
titulo	String	Título de la canción.
duracionMinutos	double	Duración en minutos.
genero	String	Género musical.
artista	Artista	Artista que interpreta la canción.
album	Album	Álbum al que pertenece (puede ser nulo).

Métodos:
Método	Parámetros	Retorno	Descripción
Cancion	int, String, double, String, Artista, Album	—	Constructor.
getId, getTitulo, getDuracionMinutos, getGenero, getArtista, getAlbum	—	varios	Métodos de acceso.
setTitulo, setDuracionMinutos, setGenero, setArtista, setAlbum	varios	void	Métodos modificadores.
toString	—	String	Representación textual.

Clase 7: Album

Clase:	Album
Tipo:	Concreta
Responsabilidad principal:	Representar un álbum musical compuesto por varias canciones de un mismo artista.

Atributos:
Atributo	Tipo de Dato	Descripción
id	int	Identificador único.
titulo	String	Título del álbum.
anio	int	Año de lanzamiento.
artista	Artista	Artista al que pertenece el álbum.
canciones	List<Cancion>	Lista de canciones que componen el álbum.

Métodos:
Método	Parámetros	Retorno	Descripción
Album	int, String, int, Artista	—	Constructor.
agregarCancion	Cancion	void	Agrega una canción al álbum.
removerCancion	int	boolean	Quita una canción por id.
getId, getTitulo, getAnio, getArtista, getCanciones	—	varios	Métodos de acceso.
toString	—	String	Representación textual.

Clase 8: IDao<T>

Clase:	IDao<T>
Tipo:	Interfaz
Responsabilidad principal:	Definir el contrato CRUD genérico que deben implementar todos los DAO del sistema, garantizando uniformidad en el acceso a datos.

Métodos:
Método	Parámetros	Retorno	Descripción
agregar	T	boolean	Agrega un nuevo objeto.
listar	—	List<T>	Retorna todos los objetos.
buscarPorId	int	T	Busca por identificador.
actualizar	T	boolean	Actualiza un objeto existente.
eliminar	int	boolean	Elimina por identificador.

Clase 9: ArtistaDAO

Clase:	ArtistaDAO
Tipo:	Concreta
Responsabilidad principal:	Encargarse exclusivamente de la persistencia de los artistas. Implementa IDao<Artista>.

Atributos:
Atributo	Tipo de Dato	Descripción
RUTA_ARCHIVO	String	Ruta del archivo de artistas.
artistas	List<Artista>	Lista en memoria con los artistas.

Métodos:
Método	Parámetros	Retorno	Descripción
ArtistaDAO	—	—	Constructor: carga la lista desde el archivo.
agregar	Artista	boolean	Agrega y guarda.
listar	—	List<Artista>	Retorna todos.
buscarPorId	int	Artista	Busca por id.
actualizar	Artista	boolean	Actualiza un artista.
eliminar	int	boolean	Elimina por id.
guardarLista	—	void	Persiste la lista.
cargarLista	—	void	Carga desde disco.

Clase 10: ProductorDAO

Clase:	ProductorDAO
Tipo:	Concreta
Responsabilidad principal:	Encargarse exclusivamente de la persistencia de los productores. Implementa IDao<Productor>.

Atributos:
Atributo	Tipo de Dato	Descripción
RUTA_ARCHIVO	String	Ruta del archivo de productores.
productores	List<Productor>	Lista en memoria.

Métodos:
Método	Parámetros	Retorno	Descripción
ProductorDAO	—	—	Constructor.
agregar	Productor	boolean	Agrega un productor.
listar	—	List<Productor>	Retorna todos.
buscarPorId	int	Productor	Busca por id.
actualizar	Productor	boolean	Actualiza.
eliminar	int	boolean	Elimina por id.
guardarLista	—	void	Persiste la lista.
cargarLista	—	void	Carga desde disco.

Clase 11: SesionDAO

Clase:	SesionDAO
Tipo:	Concreta
Responsabilidad principal:	Encargarse exclusivamente de la persistencia de las sesiones de grabación. Implementa IDao<Sesion>.

Atributos:
Atributo	Tipo de Dato	Descripción
RUTA_ARCHIVO	String	Ruta del archivo de sesiones.
sesiones	List<Sesion>	Lista en memoria.

Métodos:
Método	Parámetros	Retorno	Descripción
SesionDAO	—	—	Constructor.
agregar	Sesion	boolean	Agrega una sesión.
listar	—	List<Sesion>	Retorna todas.
buscarPorId	int	Sesion	Busca por id.
actualizar	Sesion	boolean	Actualiza.
eliminar	int	boolean	Elimina.
guardarLista	—	void	Persiste la lista.
cargarLista	—	void	Carga la lista.

Clase 12: CancionDAO

Clase:	CancionDAO
Tipo:	Concreta
Responsabilidad principal:	Encargarse exclusivamente de la persistencia de las canciones del catálogo. Implementa IDao<Cancion>.

Atributos:
Atributo	Tipo de Dato	Descripción
RUTA_ARCHIVO	String	Ruta del archivo de canciones.
canciones	List<Cancion>	Lista en memoria.

Métodos:
Método	Parámetros	Retorno	Descripción
CancionDAO	—	—	Constructor.
agregar	Cancion	boolean	Agrega una canción.
listar	—	List<Cancion>	Retorna todas.
buscarPorId	int	Cancion	Busca por id.
actualizar	Cancion	boolean	Actualiza.
eliminar	int	boolean	Elimina.
guardarLista	—	void	Persiste.
cargarLista	—	void	Carga la lista.

Clase 13: AlbumDAO

Clase:	AlbumDAO
Tipo:	Concreta
Responsabilidad principal:	Encargarse exclusivamente de la persistencia de los álbumes. Implementa IDao<Album>.

Atributos:
Atributo	Tipo de Dato	Descripción
RUTA_ARCHIVO	String	Ruta del archivo de álbumes.
albumes	List<Album>	Lista en memoria.

Métodos:
Método	Parámetros	Retorno	Descripción
AlbumDAO	—	—	Constructor.
agregar	Album	boolean	Agrega un álbum.
listar	—	List<Album>	Retorna todos.
buscarPorId	int	Album	Busca por id.
actualizar	Album	boolean	Actualiza.
eliminar	int	boolean	Elimina.
guardarLista	—	void	Persiste.
cargarLista	—	void	Carga la lista.

Clase 14: ArtistaServicio

Clase:	ArtistaServicio
Tipo:	Concreta
Responsabilidad principal:	Centralizar la lógica de negocio asociada a los artistas y servir de puente entre la vista y el DAO.

Atributos:
Atributo	Tipo de Dato	Descripción
artistaDAO	ArtistaDAO	Referencia al DAO.

Métodos:
Método	Parámetros	Retorno	Descripción
ArtistaServicio	—	—	Constructor.
registrar	String, String, String, String	Artista	Valida y crea un artista.
listar	—	List<Artista>	Retorna todos.
buscar	int	Artista	Busca por id.
buscarPorNombre	String	List<Artista>	Filtra por nombre.
editar	Artista	boolean	Actualiza un artista.
eliminar	int	boolean	Elimina un artista.

Clase 15: ProductorServicio

Clase:	ProductorServicio
Tipo:	Concreta
Responsabilidad principal:	Centralizar la lógica de negocio asociada a los productores y validar que la tarifa por hora sea correcta.

Atributos:
Atributo	Tipo de Dato	Descripción
productorDAO	ProductorDAO	Referencia al DAO.

Métodos:
Método	Parámetros	Retorno	Descripción
ProductorServicio	—	—	Constructor.
registrar	String, String, String, String, double	Productor	Valida y crea un productor.
listar	—	List<Productor>	Retorna todos.
buscar	int	Productor	Busca por id.
editar	Productor	boolean	Actualiza.
eliminar	int	boolean	Elimina.
validarTarifa	double	boolean	Verifica que sea positiva.

Clase 16: SesionServicio

Clase:	SesionServicio
Tipo:	Concreta
Responsabilidad principal:	Coordinar las sesiones de grabación: validar datos, crear sesiones, calcular costos y comunicar la vista con el DAO.

Atributos:
Atributo	Tipo de Dato	Descripción
sesionDAO	SesionDAO	Referencia al DAO.

Métodos:
Método	Parámetros	Retorno	Descripción
SesionServicio	—	—	Constructor.
crearSesion	LocalDate, double, Artista, Productor	Sesion	Valida, crea y calcula el costo.
listarSesiones	—	List<Sesion>	Retorna todas.
eliminarSesion	int	boolean	Elimina.
validarDatos	LocalDate, double, Artista, Productor	boolean	Verifica los datos.
totalIngresos	—	double	Suma los costos de todas las sesiones.

Clase 17: CancionServicio

Clase:	CancionServicio
Tipo:	Concreta
Responsabilidad principal:	Gestionar la lógica de las canciones del catálogo: registro, asignación a álbumes y filtrado por género o artista.

Atributos:
Atributo	Tipo de Dato	Descripción
cancionDAO	CancionDAO	Referencia al DAO.

Métodos:
Método	Parámetros	Retorno	Descripción
CancionServicio	—	—	Constructor.
registrar	String, double, String, Artista, Album	Cancion	Valida y crea una canción.
listar	—	List<Cancion>	Retorna todas.
buscarPorGenero	String	List<Cancion>	Filtra por género.
buscarPorArtista	Artista	List<Cancion>	Filtra por artista.
eliminar	int	boolean	Elimina una canción.

Clase 18: AlbumServicio

Clase:	AlbumServicio
Tipo:	Concreta
Responsabilidad principal:	Gestionar la lógica de los álbumes: registro, asociación de canciones y consulta del catálogo.

Atributos:
Atributo	Tipo de Dato	Descripción
albumDAO	AlbumDAO	Referencia al DAO.

Métodos:
Método	Parámetros	Retorno	Descripción
AlbumServicio	—	—	Constructor.
registrar	String, int, Artista	Album	Valida y crea un álbum.
listar	—	List<Album>	Retorna todos.
agregarCancion	int, Cancion	boolean	Agrega una canción al álbum.
eliminar	int	boolean	Elimina un álbum.

Clase 19: MainFrame

Clase:	MainFrame
Tipo:	Concreta
Responsabilidad principal:	Ser la ventana principal de la aplicación, contener el menú lateral y el área de contenido donde se muestran los demás paneles. Actúa como controlador de la navegación.

Atributos:
Atributo	Tipo de Dato	Descripción
menuLateral	JPanel	Panel con los botones del menú.
areaContenido	JPanel	Panel donde se muestran los formularios.
panelActivo	JPanel	Referencia al panel actualmente visible.
responsive	ResponsiveManager	Gestor de breakpoints para diseño responsive.

Métodos:
Método	Parámetros	Retorno	Descripción
MainFrame	—	—	Constructor: inicializa la ventana.
mostrarPanel	JPanel	void	Muestra el panel indicado.
inicializarMenu	—	void	Construye el menú lateral.
componentResized	ComponentEvent	void	Aplica responsive.ajustar() al cambiar el tamaño.

Clase 20: FormArtista

Clase:	FormArtista
Tipo:	Concreta
Responsabilidad principal:	Permitir al usuario registrar, listar, editar y eliminar artistas mediante una interfaz gráfica.

Atributos:
Atributo	Tipo de Dato	Descripción
servicio	ArtistaServicio	Referencia al servicio.
tabla	JTable	Tabla con el listado.
campoNombre, campoGenero, campoCorreo, campoTelefono	JTextField	Campos del formulario.

Métodos:
Método	Parámetros	Retorno	Descripción
FormArtista	—	—	Constructor.
guardar	—	void	Envía los datos al servicio.
editar	—	void	Carga el artista seleccionado.
eliminar	—	void	Elimina el artista.
refrescarTabla	—	void	Recarga el listado.
limpiarCampos	—	void	Limpia los campos.

Clase 21: FormProductor

Clase:	FormProductor
Tipo:	Concreta
Responsabilidad principal:	Permitir al usuario gestionar productores y su tarifa por hora mediante una interfaz gráfica.

Atributos:
Atributo	Tipo de Dato	Descripción
servicio	ProductorServicio	Referencia al servicio.
tabla	JTable	Tabla con el listado.
campoNombre, campoEspecialidad, campoCorreo, campoTelefono	JTextField	Campos del formulario.
campoTarifa	JTextField	Tarifa por hora.

Métodos:
Método	Parámetros	Retorno	Descripción
FormProductor	—	—	Constructor.
guardar	—	void	Envía los datos al servicio.
editar	—	void	Carga el productor seleccionado.
eliminar	—	void	Elimina el productor.
refrescarTabla	—	void	Recarga el listado.

Clase 22: FormSesion

Clase:	FormSesion
Tipo:	Concreta
Responsabilidad principal:	Permitir al usuario programar sesiones de grabación y mostrar el costo total calculado automáticamente al cambiar duración o productor.

Atributos:
Atributo	Tipo de Dato	Descripción
servicio	SesionServicio	Referencia al servicio.
comboArtista	JComboBox<Artista>	Selector de artista.
comboProductor	JComboBox<Productor>	Selector de productor.
campoFecha, campoDuracion	JTextField	Campos del formulario.
etiquetaCosto	JLabel	Muestra el costo total.

Métodos:
Método	Parámetros	Retorno	Descripción
FormSesion	—	—	Constructor.
guardar	—	void	Crea la sesión.
calcularYMostrarCosto	—	void	Recalcula y muestra el costo.
cargarCombos	—	void	Llena los combos con datos.

Clase 23: FormCancion

Clase:	FormCancion
Tipo:	Concreta
Responsabilidad principal:	Permitir al usuario registrar canciones en el catálogo y asociarlas a un artista y un álbum.

Atributos:
Atributo	Tipo de Dato	Descripción
servicio	CancionServicio	Referencia al servicio.
comboArtista	JComboBox<Artista>	Selector de artista.
comboAlbum	JComboBox<Album>	Selector de álbum.
campoTitulo, campoDuracion, campoGenero	JTextField	Campos del formulario.

Métodos:
Método	Parámetros	Retorno	Descripción
FormCancion	—	—	Constructor.
guardar	—	void	Registra la canción.
filtrarPorGenero	String	void	Muestra solo las canciones del género indicado.
cargarCombos	—	void	Llena los combos.

Clase 24: FormAlbum

Clase:	FormAlbum
Tipo:	Concreta
Responsabilidad principal:	Permitir al usuario registrar álbumes y administrar las canciones que los componen.

Atributos:
Atributo	Tipo de Dato	Descripción
servicio	AlbumServicio	Referencia al servicio.
comboArtista	JComboBox<Artista>	Selector de artista.
campoTitulo, campoAnio	JTextField	Campos del formulario.
tablaCanciones	JTable	Canciones del álbum.

Métodos:
Método	Parámetros	Retorno	Descripción
FormAlbum	—	—	Constructor.
guardar	—	void	Registra el álbum.
agregarCancion	—	void	Agrega una canción al álbum.
removerCancion	—	void	Quita una canción del álbum.

Clase 25: PanelChatbot

Clase:	PanelChatbot
Tipo:	Concreta
Responsabilidad principal:	Mostrar la ventana visual del asistente conversacional y conectarse con la clase Chatbot.

Atributos:
Atributo	Tipo de Dato	Descripción
chatbot	Chatbot	Referencia al motor del chatbot.
areaConversacion	JTextArea	Historial de mensajes.
campoEntrada	JTextField	Caja para escribir.
botonEnviar	JButton	Envía la pregunta.

Métodos:
Método	Parámetros	Retorno	Descripción
PanelChatbot	—	—	Constructor.
enviarMensaje	—	void	Envía el texto y muestra la respuesta.
agregarMensaje	String, String	void	Agrega un mensaje al área de conversación.

Clase 26: PanelEstadisticas

Clase:	PanelEstadisticas
Tipo:	Concreta
Responsabilidad principal:	Mostrar estadísticas operativas del estudio (sesiones por mes, ingresos por productor, género más grabado) y permitir búsqueda/filtrado avanzado de datos.

Atributos:
Atributo	Tipo de Dato	Descripción
sesionServicio	SesionServicio	Servicio de sesiones.
cancionServicio	CancionServicio	Servicio de canciones.
campoBusqueda	JTextField	Campo de búsqueda.
tablaResultados	JTable	Resultados del filtro.

Métodos:
Método	Parámetros	Retorno	Descripción
PanelEstadisticas	—	—	Constructor.
buscar	String	void	Ejecuta la búsqueda.
mostrarIngresos	—	void	Muestra el total de ingresos.
mostrarGeneroMasGrabado	—	void	Muestra el género con más sesiones.

Clase 27: DesignSystem

Clase:	DesignSystem
Tipo:	Concreta (utilitaria con miembros estáticos)
Responsabilidad principal:	Centralizar las constantes de diseño visual de la aplicación (colores, fuentes, tamaños, espaciados), garantizando consistencia en toda la interfaz.

Atributos:
Atributo	Tipo de Dato	Descripción
color_primario, color_secundario, color_fondo	Color	Colores de la aplicación.
fuente_titulo, fuente_texto	Font	Tipografías.
espaciado_pequeno, espaciado_medio, espaciado_grande	int	Espaciados en píxeles.
breakpoint_pequeno, breakpoint_medio, breakpoint_grande	int	Puntos de quiebre del diseño responsive.

Solo expone constantes estáticas; no requiere métodos de instancia.

Clase 28: Validador

Clase:	Validador
Tipo:	Concreta (utilitaria con métodos estáticos)
Responsabilidad principal:	Proveer métodos estáticos para validar los datos ingresados por el usuario, evitando duplicar lógica de validación en los servicios y formularios.

Métodos:
Método	Parámetros	Retorno	Descripción
esTextoValido	String	boolean	Verifica que la cadena no sea nula ni vacía.
esNumeroPositivo	double	boolean	Verifica que el número sea mayor a cero.
esCorreoValido	String	boolean	Verifica el formato de correo electrónico.
esFechaValida	LocalDate	boolean	Verifica que la fecha no sea pasada.

Clase 29: ResponsiveManager

Clase:	ResponsiveManager
Tipo:	Concreta
Responsabilidad principal:	Aplicar los breakpoints definidos en DesignSystem y reorganizar los componentes de la interfaz cuando el usuario redimensiona la ventana.

Atributos:
Atributo	Tipo de Dato	Descripción
ventana	JFrame	Ventana principal a gestionar.

Métodos:
Método	Parámetros	Retorno	Descripción
ResponsiveManager	JFrame	—	Constructor que recibe la ventana.
ajustar	int, int	void	Aplica el layout correspondiente según el ancho.
obtenerBreakpoint	int	String	Devuelve el breakpoint actual (pequeño/medio/grande).

Clase 30: Chatbot

Clase:	Chatbot
Tipo:	Concreta
Responsabilidad principal:	Procesar las preguntas del usuario, identificar la categoría temática a la que pertenecen y devolver una respuesta adecuada desde su base de conocimientos.

Atributos:
Atributo	Tipo de Dato	Descripción
baseConocimiento	Map<String, List<String>>	Categoría → respuestas posibles.
palabrasClave	Map<String, List<String>>	Categoría → palabras clave.

Métodos:
Método	Parámetros	Retorno	Descripción
Chatbot	—	—	Constructor: carga el JSON.
procesar	String	String	Normaliza, clasifica y responde.
normalizar	String	String	Convierte a minúsculas y elimina acentos/signos.
clasificar	String	String	Devuelve la categoría más adecuada.
obtenerRespuesta	String	String	Selecciona una respuesta aleatoria.

Relaciones entre Clases
Las relaciones entre las clases del sistema Z-one se resumen de la siguiente manera:
•Herencia: Artista y Productor se basan en la clase abstracta Persona. Esto permite reutilizar atributos y métodos comunes, evitando la duplicación de código.
•Realización: ArtistaDAO, ProductorDAO, SesionDAO, CancionDAO y AlbumDAO implementan la interfaz IDao<T>. Esto garantiza que todos tengan las mismas operaciones básicas CRUD (crear, leer, actualizar, eliminar).
•Asociación y Agregación: Sesion se asocia con Artista y Productor. Cancion se asocia con Artista y Album. Album contiene varias Canciones. Si se elimina un álbum, las canciones pueden seguir existiendo, por lo que esta relación es de agregación.
•Composición: Los Servicios contienen instancias de sus DAO correspondientes. La vida de estos DAO está completamente ligada al servicio que los contiene.
•Dependencia: Los Formularios de vista dependen de los Servicios, y los Servicios dependen de los DAO. Estas dependencias siempre se mantienen en una sola dirección.

Incorporación de Herencia, Interfaces y Clases Abstractas
El diseño utiliza los tres mecanismos del paradigma orientado a objetos de la siguiente manera:
•Clase abstracta — Persona: Se utiliza porque hay características y comportamientos comunes entre Artista y Productor. Sin embargo, no tiene sentido crear una "persona genérica" en el estudio. Al marcarla como abstracta se garantiza que solo se use como base para las subclases.
•Herencia — Artista y Productor: Ambas clases se basan en Persona, reutilizando las características comunes y agregando solo lo que es específico de cada rol.
•Interfaz — IDao<T>: Define un contrato común para los cinco DAO del sistema. Esto permite cambiar la forma en que se almacenan los datos en el futuro (por ejemplo, migrar a una base de datos relacional) sin necesidad de modificar los servicios que dependen de la interfaz.

Aplicación de Principios de Diseño Orientado a Objetos
Patrones GRASP Aplicados
Patrón	Clase donde se aplica	Razón de diseño
Creator	SesionServicio crea instancias de Sesion. AlbumServicio crea instancias de Album.	El servicio dispone de toda la información necesaria para construir el objeto válido. Se asigna la responsabilidad de crearlo a quien posee los datos.
Controller	MainFrame actúa como controlador principal de eventos.	Centraliza las acciones del usuario (clic en menús, navegación entre paneles) y delega la lógica a los servicios, evitando que la vista haga operaciones directas sobre los datos.
Information Expert	Sesion calcula su propio costo con calcularCosto().	La sesión tiene la duracionHoras y la referencia al Productor (que aporta la tarifaHora); es la "experta" en la información necesaria para calcular el costo total.
Low Coupling	Separación entre Vista, Servicio y DAO.	La vista no conoce cómo se guardan los datos y el DAO no conoce nada de la interfaz gráfica. El acoplamiento se mantiene bajo, facilitando cambios en cualquiera de las capas.
High Cohesion	Cada DAO y cada Servicio se ocupa exclusivamente de una entidad.	Los DAO no mezclan lógica de negocio y los servicios no mezclan persistencia o presentación. Cada clase cumple una única tarea relacionada con su entidad.

Principios SOLID Aplicados
Principio	Clase / Relación donde es observable	Argumentación
SRP — Responsabilidad Única	SesionDAO, SesionServicio y FormSesion tienen cada una una sola razón para cambiar.	El DAO solo cambia si cambia la forma de persistir; el servicio, si cambian las reglas de negocio; el formulario, si cambia la interfaz gráfica.
OCP — Abierto/Cerrado	La interfaz IDao<T> y sus cinco implementaciones.	El sistema está abierto a la extensión (nuevos DAO implementando IDao<T>) pero cerrado a la modificación (no es necesario tocar los DAO ni los servicios existentes).
LSP — Sustitución de Liskov	Artista y Productor pueden sustituir a Persona sin alterar el comportamiento.	Cualquier código que reciba una Persona puede recibir un Artista o un Productor y funcionar correctamente, porque ambas subclases respetan el contrato del padre.
ISP — Segregación de Interfaces	La interfaz IDao<T> es pequeña y específica.	En lugar de una interfaz enorme, se diseñó una centrada únicamente en las operaciones CRUD. Si en el futuro hicieran falta otras operaciones, se crearía una interfaz separada.
DIP — Inversión de Dependencias	Los Servicios dependen de la interfaz IDao<T>, no de las implementaciones concretas.	Los servicios trabajan contra la abstracción, de modo que se puede cambiar la implementación del DAO sin modificar la lógica del servicio.
Bibliografía
Bloch, J. (2018). Effective Java (3rd ed.). Addison-Wesley.
Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). Design Patterns: Elements of Reusable Object-Oriented Software. Addison-Wesley.
Larman, C. (2004). Applying UML and Patterns: An Introduction to Object-Oriented Analysis and Design and Iterative Development (3rd ed.). Prentice Hall.
Oracle. (2024). Java SE 17 Documentation. https://docs.oracle.com/en/java/javase/17/
Sommerville, I. (2016). Software Engineering (10th ed.). Pearson.
Pressman, R. S., & Maxim, B. R. (2019). Software Engineering: A Practitioner's Approach (9th ed.). McGraw-Hill.
