package main.usc.musicCollection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MyPanel extends JPanel implements ActionListener {

    JButton addArtistsButton, addSongsButton, newListButton, saveSongButton;
    JScrollPane playlistsScrollPane, artistsScrollPane, songsScrollPane;
    JFileChooser fileChooser;
    ArrayList<String> playlists;
    JList<String> playlistsList;
    JList<String> songsList, artistsList;

    public MyPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(123, 50, 250));

        playlists = new ArrayList<>();
        playlistsList = new JList<>(playlists.toArray(new String[0]));

        newListButton = new JButton("Nueva playlist");
        newListButton.setFocusable(false);
        newListButton.addActionListener(this);

        addArtistsButton = new JButton("Añadir artista");
        addArtistsButton.setFocusable(false);
        addArtistsButton.addActionListener(this);

        addSongsButton = new JButton("Añadir canción");
        addSongsButton.setFocusable(false);
        addSongsButton.addActionListener(this);

        saveSongButton = new JButton("Descargar");
        saveSongButton.setFocusable(false);
        saveSongButton.addActionListener(this);

        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setFileFilter(filter);

        playlistsScrollPane = new JScrollPane(playlistsList);

        String[] artistsData = {"Artista 1", "Artista 2", "Artista 3", "Artista 4"};
        artistsList = new JList<>(artistsData);
        artistsScrollPane = new JScrollPane(artistsList);

        String[] songsData = {"Cancion 1", "Cancion 2", "Cancion 3"};
        songsList = new JList<>(songsData);
        songsScrollPane = new JScrollPane(songsList);

        JPanel listsPanel = new JPanel(new BorderLayout());
        listsPanel.setPreferredSize(new Dimension(345, 100));

        JPanel playlistHeaderPanel = new JPanel(new FlowLayout());
        playlistHeaderPanel.add(new Labels("Playlist"));
        playlistHeaderPanel.add(newListButton);

        listsPanel.add(playlistHeaderPanel, BorderLayout.NORTH);
        listsPanel.add(playlistsScrollPane);

        this.add(listsPanel, BorderLayout.WEST);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new GridLayout(2, 1, 5, 5));
        eastPanel.setBackground(new Color(123, 50, 250));
        eastPanel.setPreferredSize(new Dimension(535, 100));

        JPanel artistsPanel = new JPanel(new BorderLayout());
        JPanel artistsHeaderPanel = new JPanel(new FlowLayout());
        artistsHeaderPanel.add(new Labels("Artista"));
        artistsHeaderPanel.add(addArtistsButton);
        artistsPanel.add(artistsHeaderPanel, BorderLayout.NORTH);
        artistsPanel.add(artistsScrollPane);

        JPanel songsPanel = new JPanel(new BorderLayout());
        JPanel songsHeaderPanel = new JPanel(new FlowLayout());
        songsHeaderPanel.add(new Labels("Canciones"));
        songsHeaderPanel.add(addSongsButton);
        songsHeaderPanel.add(saveSongButton);
        songsPanel.add(songsHeaderPanel, BorderLayout.NORTH);
        songsPanel.add(songsScrollPane);

        eastPanel.add(artistsPanel);
        eastPanel.add(songsPanel);
        this.add(eastPanel, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newListButton) {
            String playlistName = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva playlist:", "Nueva Playlist", JOptionPane.PLAIN_MESSAGE);
            if (playlistName != null && !playlistName.isEmpty()) {
                playlists.add(playlistName.trim());
                playlistsList.setListData(playlists.toArray(new String[0]));
                JOptionPane.showMessageDialog(this, "Playlist creada: " + playlistName, "Playlists", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        else if (e.getSource() == addArtistsButton || e.getSource() == addSongsButton) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                JList<String> targetList;
                String successMessage;

                if (e.getSource() == addArtistsButton) {
                    targetList = artistsList;
                    successMessage = "Artistas añadidos desde: " + file.getName();
                } else {
                    targetList = songsList;
                    successMessage = "Canciones añadidas desde: " + file.getName();
                }

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    java.util.List<String> currentItems = new ArrayList<>();
                    for (int i = 0; i < targetList.getModel().getSize(); i++) {
                        currentItems.add(targetList.getModel().getElementAt(i));
                    }

                    String line;
                    while ((line = reader.readLine()) != null) {
                        currentItems.add(line);
                    }

                    targetList.setListData(currentItems.toArray(new String[0]));
                }

                catch (FileNotFoundException ex) {
                    JOptionPane.showMessageDialog(this, "Archivo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                }

                catch (IOException ioException) {
                    JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
                }

                JOptionPane.showMessageDialog(this, successMessage, "Archivo procesado", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        else if (e.getSource() == saveSongButton) {
            java.util.List<String> selectedSongs = songsList.getSelectedValuesList();

            if (selectedSongs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione al menos una canción para descargar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            fileChooser.setDialogTitle("Guardar canciones");
            fileChooser.setSelectedFile(new File("canciones.txt"));
            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();

                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    for (String song : selectedSongs) {
                        writer.write(song);
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(this, "Canciones guardadas exitosamente en: " + filePath, "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }

                catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}

