Pr�-Requisitos
--------------

	1� - Para o funcionamento das Interface as packages para processamento do Import
devem ser criada na base de dados do Import-Sys e os grants devem ser dados pela equipe do IMPORT.

	2� - Os Arquivo a ser carregado pelo INOUT deve possuir a extenss�o TXT e deve esta na pasta ArquivosTXT.
	
Instru��es
-----------

1 - Alterar define.sql conforme base de dados a ser atualizada e tablespaces

2 - Incluir os arquivos da pasta JAVA deste pacote na raiz do Inout.

3 - Rodar a instala��o

3a.   [Ambiente Windows] -> Rodar o arquivo instala_win.bat.
	  [Ambiente Linux]   -> Rodar o arquivo instala_linux.sh.

4 - Inicie o InOut.

5 - Na tela "Arquivos externos" busque por config_ambiente_java.bat e edite o conteudo alterando os valores das variaveis para valores que reflitam as configura��es locais.

6 - Na tela "Arquivos externos" busque por transf_db.properties.sql alterar os hosts de origem e de destino das conex�es;

